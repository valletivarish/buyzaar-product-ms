package com.buyzaar.product.model.entity;

public class Image {
	private String fileName;
	private String altText;
	private Integer order;

	public Image() {
	}

	public Image(String fileName, String altText, Integer order) {
		this.fileName = fileName;
		this.altText = altText;
		this.order = order;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAltText() {
		return altText;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

}
