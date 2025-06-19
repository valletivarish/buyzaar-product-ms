package com.buyzaar.product.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

	@Id
	private String id;
	@Indexed(unique = true)
	private String productId;
	private String name;
	private String brand;
	private String description;
	private String category;
	private List<String> tagIds;
	private List<Specification> specifications;
	private List<Variant> variants;
	private Pricing pricing;
	private Inventory inventory;
	private String sellerId;
	private List<Image> images;
	private Rating rating;
	private List<CustomerReview> reviews;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Product() {
	}

	public Product(String id, String productId, String name, String brand, String description, String category,
			List<String> tagIds, List<Specification> specifications, List<Variant> variants, Pricing pricing,
			Inventory inventory, String sellerId, List<Image> images, Rating rating, List<CustomerReview> reviews,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.productId = productId;
		this.name = name;
		this.brand = brand;
		this.description = description;
		this.category = category;
		this.tagIds = tagIds;
		this.specifications = specifications;
		this.variants = variants;
		this.pricing = pricing;
		this.inventory = inventory;
		this.sellerId = sellerId;
		this.images = images;
		this.rating = rating;
		this.reviews = reviews;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<String> tagIds) {
		this.tagIds = tagIds;
	}

	public List<Specification> getSpecifications() {
		return specifications;
	}

	public void setSpecifications(List<Specification> specifications) {
		this.specifications = specifications;
	}

	public List<Variant> getVariants() {
		return variants;
	}

	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}

	public Pricing getPricing() {
		return pricing;
	}

	public void setPricing(Pricing pricing) {
		this.pricing = pricing;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public List<CustomerReview> getReviews() {
		return reviews;
	}

	public void setReviews(List<CustomerReview> reviews) {
		this.reviews = reviews;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(" [");

		var fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			var field = fields[i];
			field.setAccessible(true);
			sb.append(field.getName()).append("=");

			try {
				Object value = field.get(this);
				sb.append(value != null ? value : "null");
			} catch (IllegalAccessException e) {
				sb.append("<access denied>");
			}

			if (i < fields.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

}