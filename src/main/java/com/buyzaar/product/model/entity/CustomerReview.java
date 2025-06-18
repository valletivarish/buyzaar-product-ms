package com.buyzaar.product.model.entity;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerReview {
	private String reviewId;
	private String customerId;
	private String customerName;
	private Integer rating;
	private String title;
	private String comment;
	private List<Image> images;
	private LocalDateTime reviewDate;
	private Boolean verifiedPurchase;
	private Integer helpfulVotes;
	private Integer totalVotes;

	public CustomerReview() {
	}

	public CustomerReview(String reviewId, String customerId, String customerName, Integer rating, String title,
			String comment, List<Image> images, LocalDateTime reviewDate, Boolean verifiedPurchase,
			Integer helpfulVotes, Integer totalVotes) {
		this.reviewId = reviewId;
		this.customerId = customerId;
		this.customerName = customerName;
		this.rating = rating;
		this.title = title;
		this.comment = comment;
		this.images = images;
		this.reviewDate = reviewDate;
		this.verifiedPurchase = verifiedPurchase;
		this.helpfulVotes = helpfulVotes;
		this.totalVotes = totalVotes;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public LocalDateTime getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(LocalDateTime reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Boolean getVerifiedPurchase() {
		return verifiedPurchase;
	}

	public void setVerifiedPurchase(Boolean verifiedPurchase) {
		this.verifiedPurchase = verifiedPurchase;
	}

	public Integer getHelpfulVotes() {
		return helpfulVotes;
	}

	public void setHelpfulVotes(Integer helpfulVotes) {
		this.helpfulVotes = helpfulVotes;
	}

	public Integer getTotalVotes() {
		return totalVotes;
	}

	public void setTotalVotes(Integer totalVotes) {
		this.totalVotes = totalVotes;
	}
}
