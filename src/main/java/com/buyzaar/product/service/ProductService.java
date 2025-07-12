package com.buyzaar.product.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.buyzaar.product.exceptions.ImageUploadException;
import com.buyzaar.product.model.entity.Currency;
import com.buyzaar.product.model.entity.Pricing;
import com.buyzaar.product.model.entity.Product;
import com.buyzaar.product.model.entity.Specification;
import com.buyzaar.product.model.entity.Tag;
import com.buyzaar.product.model.entity.Variant;

public interface ProductService {

	List<Tag> getAllTags();

	Tag saveTag(Tag tag);

	void saveProduct(Product product) throws NoSuchAlgorithmException;

	Product getProduct(String productId) throws NoSuchAlgorithmException;

	void assignTagsForProductId(String productId, List<String> tagsIds) throws NoSuchAlgorithmException;

	void deassignTagsForProductId(String productId, List<String> tagIds) throws NoSuchAlgorithmException;

	String updatePriceForProductId(String productId, Pricing request) throws NoSuchAlgorithmException;

	List<Currency> getAllCurrencies();

	List<Product> getAllProducts(String cursorId, int limit, boolean goingBackward, String query, String category,
			List<String> tagIds, double minValue, double maxValue, boolean applyDiscountFilter,
			boolean applyInStockFilter, double averagerating, String sortBy);

	Product getProductSummary(String productId);

	List<Product> getRelatedProductsByProductId(String productId) throws NoSuchAlgorithmException;

	Long getCount();

	String toggleProductStatus(String productId) throws NoSuchAlgorithmException;

	String saveVariant(String productId, Variant variant) throws NoSuchAlgorithmException;

	String updatePriceForVariant(String productId, String variantId, Pricing request) throws NoSuchAlgorithmException;

	String uploadImageForVariant(String productId, String variantId, String originalFilename, MultipartFile file)
			throws ImageUploadException, NoSuchAlgorithmException;

	String uploadImageForProduct(String productId, MultipartFile file) throws ImageUploadException, NoSuchAlgorithmException;

	List<String> getAllImagesForProduct(String productId) throws NoSuchAlgorithmException;

	List<String> getAllImagesForVariant(String productId, String variantId) throws NoSuchAlgorithmException, ImageUploadException;

	String toggleVariantStatus(String productId, String variantId) throws NoSuchAlgorithmException;

	String deleteSpecificationForProduct(String productId, String key) throws NoSuchAlgorithmException;

	String addSpecificationForProduct(String productId, Specification request) throws NoSuchAlgorithmException;

	String addSpecificationForVariant(String productId, String variantId,
			Specification specification) throws NoSuchAlgorithmException;

}
