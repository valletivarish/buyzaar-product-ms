package com.buyzaar.product.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.buyzaar.product.constants.AppConstants;
import com.buyzaar.product.model.entity.Product;
import com.buyzaar.product.model.entity.Tag;
import com.buyzaar.product.service.ProductService;
import com.buyzaar.product.service.utils.SnowflakeIdGenerator;

@Service
public class ProductServiceImpl implements ProductService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private MongoOperations mongoOperations;

	private SnowflakeIdGenerator idGenerator;

	public ProductServiceImpl(MongoOperations mongoOperations, SnowflakeIdGenerator idGenerator) {
		this.mongoOperations = mongoOperations;
		this.idGenerator = idGenerator;
	}

	@Override
	public List<Tag> getAllTags() {
        logger.debug("Fetching all tags...");
		List<Tag> tags = mongoOperations.findAll(Tag.class);
		return tags;
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
		Criteria criteria = Criteria.where(AppConstants.PRODUCT_ID).is(product.getProductId());
		Query query = new Query(criteria);
		Product queriedProduct = mongoOperations.findOne(query, Product.class);
		logger.info("Product Queired {} {}", query, queriedProduct);
		LocalDateTime now = LocalDateTime.now();
		if (Objects.nonNull(queriedProduct)) {
            logger.debug("Updating existing product: {}", product.getProductId());
			Update update = new Update();
			Optional.ofNullable(product.getName()).ifPresent(name -> update.set(AppConstants.PRODUCT_NAME, product.getName()));
			Optional.ofNullable(product.getBrand()).ifPresent(brand -> update.set(AppConstants.PRODUCT_BRAND, product.getBrand()));
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
			mongoOperations.updateFirst(query, update, Product.class);
		} else {
            logger.debug("Creating new product: {}", product.getProductId());
			product.setCreatedAt(now);
			product.setUpdatedAt(now);
			product.setProductId(String.valueOf(idGenerator.nextId()));
			mongoOperations.save(product);
		}

	}

	@Override
	public Product getProduct(String productId) {
        logger.debug("Fetching product with ID: {}", productId);
		Criteria criteria = Criteria.where(AppConstants.PRODUCT_ID).is(productId);
		Query query = new Query(criteria); 
		Product queriedProduct = mongoOperations.findOne(query, Product.class);
		if(Objects.isNull(queriedProduct)) {
            logger.error("Product not found: {}", productId);
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST+productId);
		}
		return queriedProduct;
	}

	@Override
	public void updateTagsForProductId(String productId,List<String> tagIds) {
		Criteria criteria = Criteria.where(AppConstants.PRODUCT_ID).is(productId);
		Query query = new Query(criteria);
		Product queriedProduct = mongoOperations.findOne(query, Product.class);
		if(Objects.isNull(queriedProduct)) {
			throw new NoSuchElementException(AppConstants.PRODUCT_DOES_NOT_EXIST+productId);
		}
		List<String> retrievedtagIds = Optional.ofNullable(queriedProduct.getTagIds()).orElseGet(ArrayList::new);
		Set<String> uniqueTagIds = new LinkedHashSet<>(retrievedtagIds);
		tagIds.stream().forEach(tagId->uniqueTagIds.add(tagId));
		List<String> mergedTags = uniqueTagIds.stream().collect(Collectors.toList());
		Update update = new Update();
		update.set(AppConstants.TAGIDS, mergedTags);
		mongoOperations.updateFirst(query, update, Product.class);
		
	}

}
