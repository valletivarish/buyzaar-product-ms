package com.buyzaar.product.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.buyzaar.product.enums.ProductStatus;

public class Variant {
	private String variantId;
	private List<Specification> specifications;
	private List<Image> images;
	private Pricing pricing;
	private Inventory inventory;
	private ProductStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Variant() {
	}

	public Variant(String variantId, List<Specification> specifications, List<Image> images, Pricing pricing,
			Inventory inventory, ProductStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.variantId = variantId;
		this.specifications = specifications;
		this.images = images;
		this.pricing = pricing;
		this.inventory = inventory;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getVariantId() {
		return variantId;
	}

	public void setVariantId(String variantId) {
		this.variantId = variantId;
	}

	public List<Specification> getSpecifications() {
		return specifications;
	}

	public void setSpecifications(List<Specification> specifications) {
		this.specifications = specifications;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
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

}