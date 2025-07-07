package com.buyzaar.product.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.buyzaar.product.exceptions.ImageUploadException;
import com.buyzaar.product.model.entity.Currency;
import com.buyzaar.product.model.entity.Pricing;
import com.buyzaar.product.model.entity.Product;
import com.buyzaar.product.model.entity.Tag;
import com.buyzaar.product.model.entity.Variant;

public interface ProductService {

	List<Tag> getAllTags();

	Tag saveTag(Tag tag);

	void saveProduct(Product product);

	Product getProduct(String productId);

	void assignTagsForProductId(String productId, List<String> tagsIds);

	void deassignTagsForProductId(String productId, List<String> tagIds);

	void updatePriceForProductId(String productId, Pricing request);

	List<Currency> getAllCurrencies();

	List<Product> getAllProducts(String cursorId, int limit, boolean goingBackward, String query, String category,
			List<String> tagIds, double minValue, double maxValue, boolean applyDiscountFilter,
			boolean applyInStockFilter, double averagerating, String sortBy);

	Product getProductSummary(String productId);

	List<Product> getRelatedProductsByProductId(String productId);

	Long getCount();

	String toggleProductStatus(String productId);

	String saveVariant(String productId, Variant variant);

	String updatePriceForVariant(String productId, String variantId, Pricing request);

	String uploadImageForVariant(String productId, String variantId, String originalFilename, MultipartFile file)
			throws ImageUploadException;

}
