package com.buyzaar.product.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.buyzaar.product.constants.AppConstants;
import com.buyzaar.product.enums.ProductStatus;
import com.buyzaar.product.exceptions.ImageUploadException;
import com.buyzaar.product.model.entity.Currency;
import com.buyzaar.product.model.entity.Image;
import com.buyzaar.product.model.entity.Inventory;
import com.buyzaar.product.model.entity.Pricing;
import com.buyzaar.product.model.entity.PricingHistory;
import com.buyzaar.product.model.entity.Product;
import com.buyzaar.product.model.entity.Specification;
import com.buyzaar.product.model.entity.Tag;
import com.buyzaar.product.model.entity.Variant;
import com.buyzaar.product.service.ProductService;
import com.buyzaar.product.utils.ProductUtils;
import com.buyzaar.product.utils.SnowflakeIdGenerator;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;

@Service
public class ProductServiceImpl implements ProductService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${minio.bucket}")
	private String bucket;

	private MinioClient minioClient;

	private MongoOperations mongoOperations;

	private SnowflakeIdGenerator idGenerator;

	public ProductServiceImpl(MinioClient minioClient, MongoOperations mongoOperations,
			SnowflakeIdGenerator idGenerator) {
		this.minioClient = minioClient;
		this.mongoOperations = mongoOperations;
		this.idGenerator = idGenerator;
	}

	@Override
	public List<Tag> getAllTags() {
		return mongoOperations.findAll(Tag.class);
	}

	@Override
	public Tag saveTag(Tag tag) {
		logger.debug("Saving tag: {}", tag.getName());
		Criteria criteria = Criteria.where(AppConstants.TAG_NAME).is(tag.getName());
		Query query = new Query(criteria);

		Tag queriedTag = mongoOperations.findOne(query, Tag.class);
		LocalDateTime now = LocalDateTime.now();

		if (Objects.nonNull(queriedTag)) {
			logger.debug("Updating existing tag: {}", tag.getName());
			Update update = new Update();
			Optional.ofNullable(tag.getDescription()).ifPresent(desc -> {
				update.set(AppConstants.TAG_DESCRIPTION, desc);
				queriedTag.setDescription(desc);
			});

			Optional.ofNullable(tag.getPopularityScore()).ifPresent(score -> {
				update.set(AppConstants.TAG_POPULARITY_SCORE, score);
				queriedTag.setPopularityScore(score);
			});

			update.set(AppConstants.TAG_UPDATED_AT, now);

			mongoOperations.updateFirst(query, update, Tag.class);

			queriedTag.setUpdatedAt(LocalDateTime.now());
			return queriedTag;

		} else {
			logger.debug("Creating new tag: {}", tag.getName());
			tag.setCreatedAt(now);
			tag.setUpdatedAt(now);
			tag.setTagId(String.valueOf(idGenerator.nextId()));
			return mongoOperations.save(tag);
		}
	}

	@Override
	public void saveProduct(Product product) throws NoSuchAlgorithmException {
		logger.debug("Saving product: {}", product.getProductId());
		Query query = new Query(Criteria.where(AppConstants.PRODUCT_ID).is(product.getProductId()));
		Product queriedProduct = mongoOperations.findOne(query, Product.class);
		LocalDateTime now = LocalDateTime.now();
		if (Objects.nonNull(queriedProduct)) {
			logger.debug("Updating existing product: {}", product.getProductId());
			Update update = new Update();
			Optional.ofNullable(product.getName())
					.ifPresent(name -> update.set(AppConstants.PRODUCT_NAME, product.getName()));
			Optional.ofNullable(product.getBrand())
					.ifPresent(brand -> update.set(AppConstants.PRODUCT_BRAND, product.getBrand()));
			Optional.ofNullable(product.getCategory())
					.ifPresent(category -> update.set(AppConstants.PRODUCT_CATEGORY, product.getCategory()));
			Optional.ofNullable(product.getDescription())
					.ifPresent(desc -> update.set(AppConstants.PRODUCT_DESCRPTION, product.getDescription()));
			update.set(AppConstants.PRODUCT_UPDATED_AT, now);
			List<String> existingTagIds = Optional.ofNullable(queriedProduct.getTagIds()).orElseGet(ArrayList::new);

			Optional.ofNullable(product.getTagIds()).ifPresent(newTags -> {
				Set<String> mergedTags = new LinkedHashSet<>(existingTagIds);
				mergedTags.addAll(newTags);
				List<String> newMergedTags = mergedTags.stream().collect(Collectors.toList());
				update.set(AppConstants.TAGIDS, newMergedTags);
			});
			Optional.ofNullable(product.getPricing()).ifPresent(price -> {
				List<PricingHistory> pricingHistory = new ArrayList<>();
				pricingHistory.add(new PricingHistory(price.getSellingPrice(), LocalDateTime.now(), null));
				price.setHistory(pricingHistory);
				update.set(AppConstants.PRODUCT_PRICING, price);
			});
			mongoOperations.updateFirst(
					ProductUtils.createQuery(AppConstants.PRODUCT_ID, queriedProduct.getProductId()), update,
					Product.class);
		} else {
			logger.debug("Creating new product: {}", product.getProductId());
			product.setCreatedAt(now);
			product.setUpdatedAt(now);
			product.setStatus(ProductStatus.ACTIVE);
			product.setProductId(String.valueOf(idGenerator.nextId()));
			Optional.ofNullable(product.getPricing()).ifPresent(price -> {
				List<PricingHistory> pricingHistory = new ArrayList<>();
				pricingHistory.add(new PricingHistory(price.getSellingPrice(), LocalDateTime.now(), null));
				price.setHistory(pricingHistory);
			});
			mongoOperations.save(product);
		}

	}

	@Override
	public Product getProduct(String productId) throws NoSuchAlgorithmException {
		logger.debug("Fetching product with ID: {}", productId);
		Product queriedProduct = fetchProductById(productId);
		logger.info("Product {}", queriedProduct);

		return queriedProduct;
	}

	@Override
	public void assignTagsForProductId(String productId, List<String> tagIds) throws NoSuchAlgorithmException {
		Product queriedProduct = fetchProductById(productId);

		Query tagQuery = new Query(Criteria.where(AppConstants.TAG_ID).in(tagIds));
		List<Tag> tags = mongoOperations.find(tagQuery, Tag.class);
		LinkedHashSet<String> eligibleTags = tags.stream()
				.filter(tag -> AppConstants.SELLER_DEFINED_TAGS.contains(tag.getName())).map(Tag::getTagId)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		List<String> existingTagIds = Optional.ofNullable(queriedProduct.getTagIds()).orElseGet(ArrayList::new);
		existingTagIds.addAll(eligibleTags);
		Update update = new Update();
		update.set(AppConstants.TAGIDS, new ArrayList<>(existingTagIds));
		mongoOperations.updateFirst(ProductUtils.createQuery(AppConstants.PRODUCT_ID, productId), update,
				Product.class);
	}

	@Override
	public void deassignTagsForProductId(String productId, List<String> tagIds) throws NoSuchAlgorithmException {
		Product queriedProduct = fetchProductById(productId);
		List<String> tags = Optional.ofNullable(queriedProduct).map(Product::getTagIds).orElseGet(ArrayList::new);
		tags.removeAll(tagIds);
	}

	private Product fetchProductById(String productId) throws NoSuchAlgorithmException {
		Query query = new Query(Criteria.where(AppConstants.PRODUCT_ID).is(productId));
		Product product = mongoOperations.findOne(query, Product.class);
		if (Objects.isNull(product)) {
			throw new NoSuchAlgorithmException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}
		return product;
	}

	@Override
	public String updatePriceForProductId(String productId, Pricing request) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);
		Pricing existingPricing = Objects.nonNull(product.getPricing()) ? product.getPricing() : new Pricing();
		product.setPricing(existingPricing);

		boolean isUpdated = false;

		if (Objects.nonNull(request.getMrp()) && !Objects.equals(existingPricing.getMrp(), request.getMrp())) {
			existingPricing.setMrp(request.getMrp());
			isUpdated = true;
		}

		if (Objects.nonNull(request.getSellingPrice())
				&& !Objects.equals(existingPricing.getSellingPrice(), request.getSellingPrice())) {
			List<PricingHistory> history = existingPricing.getHistory();
			if (Objects.nonNull(history) && !history.isEmpty()) {
				PricingHistory last = history.get(history.size() - 1);
				last.setToDate(LocalDateTime.now());
			} else {
				history = new ArrayList<>();
				existingPricing.setHistory(history);
			}

			PricingHistory newHistory = new PricingHistory(request.getSellingPrice(), LocalDateTime.now(), null);
			history.add(newHistory);

			existingPricing.setSellingPrice(request.getSellingPrice());
			product.setUpdatedAt(LocalDateTime.now());
			isUpdated = true;
		}

		if (Objects.nonNull(request.getCurrencyCode())
				&& !Objects.equals(existingPricing.getCurrencyCode(), request.getCurrencyCode())) {
			existingPricing.setCurrencyCode(request.getCurrencyCode());
			isUpdated = true;
		}

		if (!isUpdated) {
			return "No pricing changes detected for product ID " + productId;
		}

		Update update = new Update();
		update.set(AppConstants.PRODUCT_PRICING, product.getPricing());
		update.set(AppConstants.PRODUCT_UPDATED_AT, product.getUpdatedAt());

		mongoOperations.updateFirst(ProductUtils.createQuery(AppConstants.PRODUCT_ID, productId), update,
				Product.class);

		return "Price updated successfully for product ID " + productId;
	}

	@Override
	public List<Currency> getAllCurrencies() {
		Query query = new Query().with(Sort.by(Sort.Direction.ASC, "_id"));
		return mongoOperations.find(query, Currency.class);
	}

	@Override
	public List<Product> getAllProducts(String cursorId, int limit, boolean goingBackward, String searchQuery,
			String category, List<String> tagIds, double minValue, double maxValue, boolean applyDiscountFilter,
			boolean applyInStockFilter, double averagerating, String sortBy) {
		Query query = new Query();

		applyCursorFilter(query, cursorId, goingBackward);

		applySearchQueryFilter(query, searchQuery);

		applyCategoryFilter(query, category);

		applyTagsFilter(query, tagIds);

		applyDiscountFilter(query, applyDiscountFilter);

		applyMinimumAndMaximumFilter(query, minValue, maxValue);

		applyInStockFilter(query, applyInStockFilter);

		applyMinimumRatingFilter(query, averagerating);
		Sort sortByClause = getSortByClause(sortBy, goingBackward);
		query.with(sortByClause);

		query.limit(limit);

		List<Product> products = mongoOperations.find(query, Product.class);

		if (goingBackward) {
			Collections.reverse(products);
		}

		return products;
	}

	private Sort getSortByClause(String sortBy, boolean goingBackward) {
		Sort.Direction direction = goingBackward ? Sort.Direction.DESC : Sort.Direction.ASC;

		if (Objects.isNull(sortBy) || sortBy.isBlank()) {
			return Sort.by(direction, AppConstants.PRODUCT_ID);
		}

		switch (sortBy.trim().toLowerCase()) {
		case "price_low_to_high":
			return Sort.by(Sort.Direction.ASC, AppConstants.PRODUCT_SELLING_PRICE)
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "price_high_to_low":
			return Sort.by(Sort.Direction.DESC, AppConstants.PRODUCT_SELLING_PRICE)
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "rating_high_to_low":
			return Sort.by(Sort.Direction.DESC, AppConstants.PRODUCT_AVERAGE_RATING)
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "rating_low_to_high":
			return Sort.by(Sort.Direction.ASC, AppConstants.PRODUCT_AVERAGE_RATING)
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "newest":
			return Sort.by(Sort.Direction.DESC, AppConstants.PRODUCT_CREATED_AT)
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "oldest":
			return Sort.by(Sort.Direction.ASC, AppConstants.PRODUCT_CREATED_AT)
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "discount":
			return Sort.by(Sort.Direction.DESC, "pricing.discountPercentage")
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "popularity":
			return Sort.by(Sort.Direction.DESC, "ratingCount").and(Sort.by(Sort.Direction.DESC, "averageRating"))
					.and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "alphabetical_asc":
			return Sort.by(Sort.Direction.ASC, "name").and(Sort.by(direction, AppConstants.PRODUCT_ID));

		case "alphabetical_desc":
			return Sort.by(Sort.Direction.DESC, "name").and(Sort.by(direction, AppConstants.PRODUCT_ID));

		default:
			return Sort.by(direction, AppConstants.PRODUCT_ID);
		}
	}

	private void applyMinimumRatingFilter(Query query, double averageRating) {
		logger.info("Applying minimum rating filter:  {}", averageRating);
		if (averageRating > 0) {
			query.addCriteria(Criteria.where("averageRating").gte(averageRating));
		}
	}

	private void applyInStockFilter(Query query, boolean applyInStockFilter) {
		logger.info("Applying inStock filter: {}", applyInStockFilter);
		if (applyInStockFilter) {
			query.addCriteria(Criteria.where("inventory.inStock").is(applyInStockFilter));
		}
	}

	private void applyMinimumAndMaximumFilter(Query query, double minValue, double maxValue) {
		logger.info("Applying price range filter: minValue={} maxValue={}", minValue, maxValue);
		if (minValue < maxValue) {
			query.addCriteria(Criteria.where("pricing.sellingPrice").gte(minValue).lte(maxValue));
		}
	}

	private void applyDiscountFilter(Query query, boolean applyDiscountFilter) {
		logger.info("Applying discount filter: {}", applyDiscountFilter);
		if (applyDiscountFilter) {
			Document exprDoc = new Document("$gt", List.of("$pricing.mrp", "$pricing.sellingPrice"));
			query.addCriteria(Criteria.where("$expr").is(exprDoc));
		}
	}

	private void applyTagsFilter(Query query, List<String> tagIds) {
		logger.info("Applying tagIds filter with tagIds={}", tagIds);
		if (Objects.nonNull(tagIds) && !tagIds.isEmpty()) {
			Criteria tagIdsCriteria = Criteria.where(AppConstants.TAGIDS).in(tagIds);
			query.addCriteria(tagIdsCriteria);
		}
	}

	private void applyCategoryFilter(Query query, String category) {
		logger.info("Applying category filter with category={}", category);
		if (Objects.nonNull(category) && !category.isBlank()) {
			Criteria categoryCriteria = Criteria.where(AppConstants.PRODUCT_CATEGORY).regex(category, "i");
			query.addCriteria(categoryCriteria);
		}

	}

	private void applySearchQueryFilter(Query query, String searchQuery) {
		if (Objects.nonNull(searchQuery) && !searchQuery.isBlank()) {
			logger.info("Applying search filter on name and description for query: '{}'", searchQuery);

			Criteria nameFilter = Criteria.where(AppConstants.PRODUCT_NAME).regex(searchQuery, "i");
			Criteria descriptionFilter = Criteria.where(AppConstants.PRODUCT_DESCRPTION).regex(searchQuery, "i");

			query.addCriteria(nameFilter);
			query.addCriteria(descriptionFilter);
		} else {
			logger.debug("No search query provided, skipping search filter.");
		}
	}

	private void applyCursorFilter(Query query, String cursorId, boolean goingBackward) {
		logger.info("Applying cursor filter with cursorId={} and goingBackward={}", cursorId, goingBackward);
		if (Objects.nonNull(cursorId) && !cursorId.isBlank()) {
			Criteria cursorCriteria = goingBackward ? Criteria.where(AppConstants.PRODUCT_ID).lt(cursorId)
					: Criteria.where(AppConstants.PRODUCT_ID).gt(cursorId);
			query.addCriteria(cursorCriteria);
		}
	}

	@Override
	public Product getProductSummary(String productId) {
		Query query = ProductUtils.createQuery(AppConstants.PRODUCT_ID, productId);
		query.fields().include(AppConstants.PRODUCT_NAME).include(AppConstants.PRODUCT_BRAND)
				.include("pricing.sellingPrice").include("pricing.mrp");
		return mongoOperations.findOne(query, Product.class);
	}

	@Override
	public List<Product> getRelatedProductsByProductId(String productId) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);
		Query query = new Query();

		List<String> categories = Objects.nonNull(product.getCategory()) && !product.getCategory().isEmpty()
				? product.getCategory()
				: new ArrayList<>();

		if (categories.isEmpty())
			return new ArrayList<>();

		query.addCriteria(Criteria.where("productId").ne(productId));
		query.addCriteria(Criteria.where(AppConstants.PRODUCT_CATEGORY).in(categories));
		query.limit(10);
		logger.info("Query {}", query);
		List<Product> allRelatedProducts = mongoOperations.find(query, Product.class);
		Collections.shuffle(allRelatedProducts);
		return allRelatedProducts.stream().limit(10).collect(Collectors.toList());
	}

	@Override
	public Long getCount() {
		return mongoOperations.count(new Query(), Product.class);
	}

	@Override
	public String toggleProductStatus(String productId) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);

		ProductStatus currentStatus = product.getStatus();
		ProductStatus newStatus = (currentStatus == ProductStatus.ACTIVE) ? ProductStatus.INACTIVE
				: ProductStatus.ACTIVE;

		product.setStatus(newStatus);
		mongoOperations.save(product);

		return "Product status updated to: " + newStatus;
	}

	@Override
	public String saveVariant(String productId, Variant variant) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);

		if (Objects.isNull(variant)) {
			throw new IllegalArgumentException("Variant cannot be null");
		}

		variant.setSpecifications(
				Optional.ofNullable(variant.getSpecifications()).orElseGet(ArrayList<Specification>::new));
		variant.setImages(Optional.ofNullable(variant.getImages()).orElseGet(ArrayList::new));
		variant.setInventory(Optional.ofNullable(variant.getInventory()).orElseGet(Inventory::new));
		variant.setPricing(Optional.ofNullable(variant.getPricing()).orElseGet(Pricing::new));
		variant.setVariantId(String.valueOf(idGenerator.nextId()));
		variant.setCreatedAt(LocalDateTime.now());
		variant.setUpdatedAt(LocalDateTime.now());
		variant.setStatus(ProductStatus.ACTIVE);

		Pricing pricing = variant.getPricing();
		List<PricingHistory> history = Optional.ofNullable(pricing.getHistory()).orElse(new ArrayList<>());

		if (Objects.nonNull(pricing.getSellingPrice())) {
			history.add(new PricingHistory(pricing.getSellingPrice(), LocalDateTime.now(), null));
		}

		pricing.setHistory(history);
		variant.setPricing(pricing);

		List<Variant> variants = Optional.ofNullable(product.getVariants()).orElseGet(ArrayList::new);
		variants.add(variant);
		product.setVariants(variants);

		mongoOperations.save(product);

		return "Variant saved successfully with ID: " + variant.getVariantId();
	}

	@Override
	public String updatePriceForVariant(String productId, String variantId, Pricing request)
			throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);

		List<Variant> variants = Optional.ofNullable(product.getVariants())
				.orElseThrow(() -> new NoSuchElementException("No variants found for product: " + productId));

		for (Variant variant : variants) {
			if (variantId.equals(variant.getVariantId())) {
				Pricing existingPricing = Objects.nonNull(variant.getPricing()) ? variant.getPricing() : new Pricing();
				boolean variantUpdated = false;

				if (Objects.nonNull(request.getMrp()) && !Objects.equals(existingPricing.getMrp(), request.getMrp())) {
					existingPricing.setMrp(request.getMrp());
					variantUpdated = true;
				}

				if (Objects.nonNull(request.getSellingPrice())
						&& !Objects.equals(existingPricing.getSellingPrice(), request.getSellingPrice())) {
					List<PricingHistory> history = Objects.nonNull(existingPricing.getHistory())
							? existingPricing.getHistory()
							: new ArrayList<>();
					if (!history.isEmpty()) {
						history.get(history.size() - 1).setToDate(LocalDateTime.now());
					}
					history.add(new PricingHistory(request.getSellingPrice(), LocalDateTime.now(), null));
					existingPricing.setSellingPrice(request.getSellingPrice());
					existingPricing.setHistory(history);
					variantUpdated = true;
				}

				if (Objects.nonNull(request.getCurrencyCode())
						&& !Objects.equals(existingPricing.getCurrencyCode(), request.getCurrencyCode())) {
					existingPricing.setCurrencyCode(request.getCurrencyCode());
					variantUpdated = true;
				}

				if (!variantUpdated) {
					return "No pricing changes detected for variant ID: " + variantId + " in product ID: " + productId;
				}

				variant.setPricing(existingPricing);
				variant.setUpdatedAt(LocalDateTime.now());
				mongoOperations.save(product);
				return "Pricing updated successfully for variant ID: " + variantId + " in product ID: " + productId;
			}
		}

		throw new NoSuchElementException("Variant not found with ID: " + variantId);
	}

	@Override
	public String uploadImageForVariant(String productId, String variantId, String originalFilename, MultipartFile file)
			throws ImageUploadException, NoSuchAlgorithmException {
		Product product = fetchProductById(productId);

		List<Variant> variants = product.getVariants();
		if (Objects.isNull(variants) || variants.isEmpty()) {
			throw new IllegalStateException("No variants found for product " + productId);
		}
		String fileName = "";

		for (int i = 0; i < variants.size(); i++) {
			Variant variant = variants.get(i);
			if (variant.getVariantId().equals(variantId)) {

				List<Image> images = Optional.ofNullable(variant.getImages()).orElseGet(ArrayList::new);

				int nextOrder = images.stream().map(Image::getOrder).max(Integer::compareTo).orElse(0) + 1;

				fileName = productId + "_" + variantId + "_" + images.size();
				String altText = ProductUtils.generateAltText(product.getName(), variant.getSpecifications());

				Image image = new Image(fileName, altText, nextOrder);
				images.add(image);

				try (InputStream inputStream = file.getInputStream()) {
					minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(fileName)
							.stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build());

					variant.setImages(images);
					variants.set(i, variant);
					mongoOperations.save(product);

					logger.info("Image uploaded successfully: {}, order: {}, altText: {}", fileName, nextOrder,
							altText);

				} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
						| InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
						| IllegalArgumentException | IOException e) {
					throw new ImageUploadException("Image upload failed for product " + productId, e);
				}
			}
		}

		return "Image uploaded successfully for variant " + variantId + " with name " + fileName;
	}

	@Override
	public String uploadImageForProduct(String productId, MultipartFile file)
			throws ImageUploadException, NoSuchAlgorithmException {
		Product product = fetchProductById(productId);

		List<Image> images = Optional.ofNullable(product.getImages()).orElseGet(ArrayList::new);
		int nextOrder = images.stream().map(Image::getOrder).max(Integer::compareTo).orElse(0) + 1;

		String fileName = productId + "_" + images.size();
		String altText = ProductUtils.generateAltText(product.getName(), product.getSpecifications());

		Image image = new Image(fileName, altText, nextOrder);
		images.add(image);
		try (InputStream inputStream = file.getInputStream()) {
			minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(fileName)
					.stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build());
			product.setImages(images);
			mongoOperations.save(product);
		} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
				| InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
				| IllegalArgumentException | IOException e) {
			throw new ImageUploadException("Image upload failed for product " + productId, e);
		}

		return "Image uploaded successfully for product " + productId + " with name " + fileName;
	}

	@Override
	public List<String> getAllImagesForProduct(String productId) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);
		List<Image> images = Optional.ofNullable(product.getImages()).orElseGet(ArrayList::new);

		return images.stream().map(image -> {
			try {
				return generatePresignedUrl(image.getFileName());
			} catch (ImageUploadException e) {
				logger.warn("Skipping image [{}] due to error generating URL: {}", image.getFileName(), e.getMessage());
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllImagesForVariant(String productId, String variantId) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);

		Optional<Variant> matchedVariant = product.getVariants().stream()
				.filter(variant -> variant.getVariantId().equals(variantId)).findFirst();

		List<Image> images = matchedVariant.map(Variant::getImages).orElseGet(ArrayList::new);

		return images.stream().map(image -> {
			try {
				return generatePresignedUrl(image.getFileName());
			} catch (ImageUploadException e) {
				logger.error("Failed to generate presigned URL for file: {}", image.getFileName(), e);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private String generatePresignedUrl(String fileName) throws ImageUploadException {
		try {
			return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucket).object(fileName)
					.expiry(60 * 60).method(Method.GET).build());
		} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
				| InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
				| IllegalArgumentException | IOException e) {
			throw new ImageUploadException("Failed to generate presigned URL for " + fileName, e);
		}
	}

	@Override
	public String toggleVariantStatus(String productId, String variantId) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);
		List<Variant> variants = product.getVariants();

		for (int i = 0; i < variants.size(); i++) {
			Variant variant = variants.get(i);
			if (variant.getVariantId().equals(variantId)) {
				ProductStatus oldStatus = variant.getStatus();
				ProductStatus newStatus = oldStatus.equals(ProductStatus.ACTIVE) ? ProductStatus.INACTIVE
						: ProductStatus.ACTIVE;
				variant.setStatus(newStatus);
				variants.set(i, variant);

				product.setVariants(variants);
				mongoOperations.save(product);

				return "Variant status updated to: " + newStatus;
			}
		}

		throw new NoSuchElementException("Variant with ID " + variantId + " not found in product " + productId);
	}

	@Override
	public String deleteSpecificationForProduct(String productId, String key) throws NoSuchAlgorithmException {
		Product product = fetchProductById(productId);
		List<Specification> specifications = product.getSpecifications();

		for (int i = 0; i < specifications.size(); i++) {
			if (specifications.get(i).getKey().equalsIgnoreCase(key)) {
				specifications.remove(i);
				product.setSpecifications(specifications);
				mongoOperations.save(product);
				return "Specification with key '" + key + "' deleted successfully for product ID: " + productId;
			}
		}

		throw new NoSuchElementException("Specification with key '" + key + "' not found in product ID: " + productId);
	}

	@Override
	public String addSpecificationForProduct(String productId, Specification request) throws NoSuchAlgorithmException {
		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("Specification request cannot be null");
		}

		if (Objects.isNull(request.getKey()) || request.getKey().isBlank()) {
			throw new IllegalArgumentException("Specification key cannot be null or blank");
		}

		if (Objects.isNull(request.getValue()) || request.getValue().isBlank()) {
			throw new IllegalArgumentException("Specification value cannot be null or blank");
		}

		Product product = fetchProductById(productId);
		List<Specification> specifications = Optional.ofNullable(product.getSpecifications()).orElseGet(ArrayList::new);

		Specification newSpecification = new Specification();
		newSpecification.setKey(request.getKey());
		newSpecification.setValue(request.getValue());

		specifications.add(newSpecification);
		product.setSpecifications(specifications);
		mongoOperations.save(product);

		return "Specification added successfully for product ID: " + productId;
	}

	@Override
	public String addSpecificationForVariant(String productId, String variantId, Specification specification)
			throws NoSuchAlgorithmException {

		if (Objects.isNull(specification)) {
			throw new IllegalArgumentException(
					"Specification object must not be null. Please provide valid specification data.");
		}

		if (Objects.isNull(specification.getKey()) || specification.getKey().isBlank()) {
			throw new IllegalArgumentException("Missing specification key: key must not be null or empty.");
		}

		if (Objects.isNull(specification.getValue()) || specification.getValue().isBlank()) {
			throw new IllegalArgumentException("Missing specification value: value must not be null or empty.");
		}

		Product product = fetchProductById(productId);
		List<Variant> variants = Optional.ofNullable(product.getVariants()).orElseGet(ArrayList::new);

		for (int i = 0; i < variants.size(); i++) {
			Variant variant = variants.get(i);
			if (variant.getVariantId().equals(variantId)) {
				List<Specification> specifications = Optional.ofNullable(variant.getSpecifications())
						.orElseGet(ArrayList::new);

				Specification newSpecification = new Specification();
				newSpecification.setKey(specification.getKey());
				newSpecification.setValue(specification.getValue());

				specifications.add(newSpecification);
				variant.setSpecifications(specifications);
				variants.set(i, variant);
				product.setVariants(variants);
				mongoOperations.save(product);

				return String.format("Specification added successfully for Variant (ID: %s).", variantId);
			}
		}

		throw new NoSuchAlgorithmException(String.format(
				"Variant (ID: %s) not found under Product (ID: %s). Please verify that the variant exists and is properly mapped in the catalog.",
				variantId, productId));
	}

}
