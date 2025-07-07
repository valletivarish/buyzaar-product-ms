package com.buyzaar.product.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.buyzaar.product.model.entity.Tag;
import com.buyzaar.product.model.entity.Variant;
import com.buyzaar.product.service.ProductService;
import com.buyzaar.product.service.utils.ProductUtils;
import com.buyzaar.product.service.utils.SnowflakeIdGenerator;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

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
	public void saveProduct(Product product) {
		logger.debug("Saving product: {}", product.getProductId());
		Product queriedProduct = fetchProductById(product.getProductId());
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
	public Product getProduct(String productId) {
		logger.debug("Fetching product with ID: {}", productId);
		Product queriedProduct = fetchProductById(productId);
		logger.info("Product {}", queriedProduct);
		if (Objects.isNull(queriedProduct)) {
			logger.error("Product not found: {}", productId);
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}
		return queriedProduct;
	}

	@Override
	public void assignTagsForProductId(String productId, List<String> tagIds) {
		Product queriedProduct = fetchProductById(productId);
		if (Objects.isNull(queriedProduct)) {
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}
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
	public void deassignTagsForProductId(String productId, List<String> tagIds) {
		Product queriedProduct = fetchProductById(productId);
		List<String> tags = Optional.ofNullable(queriedProduct).map(Product::getTagIds).orElseGet(ArrayList::new);
		tags.removeAll(tagIds);
	}

	private Product fetchProductById(String productId) {
		Query query = new Query(Criteria.where(AppConstants.PRODUCT_ID).is(productId));
		return mongoOperations.findOne(query, Product.class);
	}

	@Override
	public void updatePriceForProductId(String productId, Pricing request) {
		Product product = fetchProductById(productId);
		if (Objects.nonNull(product)) {
			Optional.ofNullable(request.getMrp()).ifPresent(mrp -> product.getPricing().setMrp(mrp));
			Optional.ofNullable(request.getSellingPrice()).ifPresent(sellingPrice -> {
				List<PricingHistory> history = product.getPricing().getHistory();
				if (history != null && !history.isEmpty()) {
					PricingHistory last = history.get(history.size() - 1);
					last.setToDate(LocalDateTime.now());
				} else {
					history = new ArrayList<>();
					product.getPricing().setHistory(history);
				}

				PricingHistory newHistory = new PricingHistory(sellingPrice, LocalDateTime.now(), null);
				history.add(newHistory);
				product.getPricing().setSellingPrice(sellingPrice);
			});
			Update update = new Update();
			update.set(AppConstants.PRODUCT_PRICING, product.getPricing());
			mongoOperations.updateFirst(ProductUtils.createQuery(AppConstants.PRODUCT_ID, productId), update,
					Product.class);

		}
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
	public List<Product> getRelatedProductsByProductId(String productId) {
		Product product = fetchProductById(productId);
		if (Objects.nonNull(product)) {
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
		return new ArrayList<>();
	}

	@Override
	public Long getCount() {
		return mongoOperations.count(new Query(), Product.class);
	}

	@Override
	public String toggleProductStatus(String productId) {
		Product product = fetchProductById(productId);

		if (Objects.isNull(product)) {
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}

		ProductStatus currentStatus = product.getStatus();
		ProductStatus newStatus = (currentStatus == ProductStatus.ACTIVE) ? ProductStatus.INACTIVE
				: ProductStatus.ACTIVE;

		product.setStatus(newStatus);
		mongoOperations.save(product);

		return "Product status updated to: " + newStatus;
	}

	@Override
	public String saveVariant(String productId, Variant variant) {
		Product product = fetchProductById(productId);
		if (Objects.isNull(product)) {
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}

		if (Objects.isNull(variant)) {
			throw new IllegalArgumentException("Variant cannot be null");
		}

		variant.setAttributes(Optional.ofNullable(variant.getAttributes()).orElseGet(HashMap::new));
		variant.setImages(Optional.ofNullable(variant.getImages()).orElseGet(ArrayList::new));
		variant.setInventory(Optional.ofNullable(variant.getInventory()).orElseGet(Inventory::new));
		variant.setPricing(Optional.ofNullable(variant.getPricing()).orElseGet(Pricing::new));
		variant.setVariantId(String.valueOf(idGenerator.nextId()));
		variant.setCreatedAt(LocalDateTime.now());
		variant.setUpdatedAt(LocalDateTime.now());

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
	public String updatePriceForVariant(String productId, String variantId, Pricing request) {
		Product product = fetchProductById(productId);
		if (Objects.isNull(product)) {
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}

		List<Variant> variants = Optional.ofNullable(product.getVariants())
				.orElseThrow(() -> new NoSuchElementException("No variants found for product: " + productId));

		boolean updated = false;

		for (Variant variant : variants) {
			if (variantId.equals(variant.getVariantId())) {
				Pricing pricing = Optional.ofNullable(variant.getPricing()).orElse(new Pricing());

				Optional.ofNullable(request.getMrp()).ifPresent(pricing::setMrp);

				Optional.ofNullable(request.getSellingPrice()).ifPresent(sellingPrice -> {
					List<PricingHistory> history = Optional.ofNullable(pricing.getHistory()).orElse(new ArrayList<>());

					if (!history.isEmpty()) {
						history.get(history.size() - 1).setToDate(LocalDateTime.now());
					}

					history.add(new PricingHistory(sellingPrice, LocalDateTime.now(), null));
					pricing.setSellingPrice(sellingPrice);
					pricing.setHistory(history);
				});

				Optional.ofNullable(request.getCurrencyCode()).ifPresent(pricing::setCurrencyCode);

				variant.setPricing(pricing);
				updated = true;
				break;
			}
		}

		if (!updated) {
			throw new NoSuchElementException("Variant not found with ID: " + variantId);
		}

		mongoOperations.save(product);
		return "Pricing updated successfully for variant ID: " + variantId + " in product ID: " + productId;
	}

	@Override
	public String uploadImageForVariant(String productId, String variantId, String originalFilename, MultipartFile file)
			throws ImageUploadException {
		Product product = fetchProductById(productId);
		if (Objects.isNull(product)) {
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST + productId);
		}

		List<Variant> variants = product.getVariants();
		if (Objects.isNull(variants) || variants.isEmpty()) {
			throw new IllegalStateException("No variants found for product " + productId);
		}

		for (int i = 0; i < variants.size(); i++) {
			Variant variant = variants.get(i);
			if (variant.getVariantId().equals(variantId)) {

				List<Image> images = Optional.ofNullable(variant.getImages()).orElseGet(ArrayList::new);

				int nextOrder = images.stream().map(Image::getOrder).max(Integer::compareTo).orElse(0) + 1;

				String fileName = productId + "_" + variantId + "_" + images.size();
				String altText = generateAltText(product, variant);

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
					return "Image uploaded successfully for variant " + variantId + " with name " + fileName;
				} catch (Exception e) {
					logger.error("Failed to upload image '{}' for variant '{}'", fileName, variantId, e);
					throw new ImageUploadException("Image upload failed for variant " + variantId, e);
				}
			}
		}

		throw new NoSuchElementException("Variant with ID " + variantId + " not found in product " + productId);
	}

	public String generateAltText(Product product, Variant variant) {
		StringBuilder altText = new StringBuilder("Image of ");
		altText.append(product.getName());

		Map<String, String> specs = variant.getAttributes();

		if (specs != null && !specs.isEmpty()) {
			altText.append(" - ");
			altText.append(specs.entrySet().stream().map(Entry::getValue).collect(Collectors.joining(", ")));
		}

		return altText.toString();
	}

}
