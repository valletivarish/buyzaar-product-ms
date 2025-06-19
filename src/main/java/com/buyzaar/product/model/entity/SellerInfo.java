package com.buyzaar.product.model.entity;

public class SellerInfo {
	private String sellerId;
	private String sellerName;
	private String contactEmail;
	private String contactPhone;

	private Double averageRating;
	private Integer numberOfRatings;

	private Integer lateDeliveriesCount;
	private Integer cancelledOrdersCount;
	private Integer completedOrdersCount;
	private Integer returnedOrdersCount;

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getNumberOfRatings() {
		return numberOfRatings;
	}

	public void setNumberOfRatings(Integer numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	public Integer getLateDeliveriesCount() {
		return lateDeliveriesCount;
	}

	public void setLateDeliveriesCount(Integer lateDeliveriesCount) {
		this.lateDeliveriesCount = lateDeliveriesCount;
	}

	public Integer getCancelledOrdersCount() {
		return cancelledOrdersCount;
	}

	public void setCancelledOrdersCount(Integer cancelledOrdersCount) {
		this.cancelledOrdersCount = cancelledOrdersCount;
	}

	public Integer getCompletedOrdersCount() {
		return completedOrdersCount;
	}

	public void setCompletedOrdersCount(Integer completedOrdersCount) {
		this.completedOrdersCount = completedOrdersCount;
	}

	public Integer getReturnedOrdersCount() {
		return returnedOrdersCount;
	}

	public void setReturnedOrdersCount(Integer returnedOrdersCount) {
		this.returnedOrdersCount = returnedOrdersCount;
	}

	public SellerInfo() {
	}

	public SellerInfo(String sellerId, String sellerName, String contactEmail, String contactPhone,
			Double averageRating, Integer numberOfRatings, Integer lateDeliveriesCount, Integer cancelledOrdersCount,
			Integer completedOrdersCount, Integer returnedOrdersCount) {
		this.sellerId = sellerId;
		this.sellerName = sellerName;
		this.contactEmail = contactEmail;
		this.contactPhone = contactPhone;
		this.averageRating = averageRating;
		this.numberOfRatings = numberOfRatings;
		this.lateDeliveriesCount = lateDeliveriesCount;
		this.cancelledOrdersCount = cancelledOrdersCount;
		this.completedOrdersCount = completedOrdersCount;
		this.returnedOrdersCount = returnedOrdersCount;
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