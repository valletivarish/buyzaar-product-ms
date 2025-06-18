package com.buyzaar.product.model.dto;

public class InputRequest<T> {
	private T request;

	public InputRequest() {
	}

	public InputRequest(T request) {
		this.request = request;
	}

	public T getRequest() {
		return request;
	}

	public void setRequest(T request) {
		this.request = request;
	}
}
