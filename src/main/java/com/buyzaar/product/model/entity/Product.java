package com.buyzaar.product.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.buyzaar.product.enums.ProductStatus;

@Document(collection = "products")
@CompoundIndexes({ @CompoundIndex(name = "pricing_sellingPrice_idx", def = "{'pricing.sellingPrice': 1}"),
		@CompoundIndex(name = "inventory_inStock_idx", def = "{'inventory.inStock': 1}"),
		@CompoundIndex(name = "averageRating_idx", def = "{'averageRating': 1}") })
public class Product {

	@Id
	@Indexed(unique = true)
	private String productId;
	@Indexed
	private String name;
	@Indexed
	private String brand;
	@Indexed
	private String description;
	@Indexed
	private List<String> category;
	@Indexed
	private List<String> tagIds;
	@Indexed
	private List<Specification> specifications;
	private List<Variant> variants;
	private Pricing pricing;
	private String sellerId;
	private List<Image> images;
	private Inventory inventory;
	private Double averageRating = 0.0;
	private Integer ratingCount = 0;
	private List<CustomerReview> reviews;
	private ProductStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Product() {
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

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
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

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public List<CustomerReview> getReviews() {
		return reviews;
	}

	public void setReviews(List<CustomerReview> reviews) {
		this.reviews = reviews;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
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

	public Product(String productId, String name, String brand, String description, List<String> category,
			List<String> tagIds, List<Specification> specifications, List<Variant> variants, Pricing pricing,
			String sellerId, List<Image> images, Inventory inventory, Double averageRating, Integer ratingCount,
			List<CustomerReview> reviews, ProductStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.productId = productId;
		this.name = name;
		this.brand = brand;
		this.description = description;
		this.category = category;
		this.tagIds = tagIds;
		this.specifications = specifications;
		this.variants = variants;
		this.pricing = pricing;
		this.sellerId = sellerId;
		this.images = images;
		this.inventory = inventory;
		this.averageRating = averageRating;
		this.ratingCount = ratingCount;
		this.reviews = reviews;
		this.status = status;
		this.createdAt = createdAt;
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