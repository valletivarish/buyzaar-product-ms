package com.buyzaar.product.exceptions;

public class ImageUploadException extends Exception {

	private static final long serialVersionUID = 1L;

	public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
