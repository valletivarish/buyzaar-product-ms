package com.buyzaar.product.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_stats")
public class ProductStats {

    @Id
    private String id;

    private String productId;

    private LocalDate date;

    private int purchaseCount;

    private int viewCount;

    private LocalDateTime updatedAt;

	public ProductStats(String id, String productId, LocalDate date, int purchaseCount, int viewCount,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.productId = productId;
		this.date = date;
		this.purchaseCount = purchaseCount;
		this.viewCount = viewCount;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getPurchaseCount() {
		return purchaseCount;
	}

	public void setPurchaseCount(int purchaseCount) {
		this.purchaseCount = purchaseCount;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + " [");
	    var fields = this.getClass().getDeclaredFields();
	    for (int i = 0; i < fields.length; i++) {
	        fields[i].setAccessible(true);
	        try {
	            sb.append(fields[i].getName())
	              .append("=")
	              .append(fields[i].get(this));
	        } catch (IllegalAccessException e) {
	            sb.append(fields[i].getName()).append("=<access denied>");
	        }
	        if (i < fields.length - 1) {
	            sb.append(", ");
	        }
	    }
	    sb.append("]");
	    return sb.toString();
	}

    
    


}
