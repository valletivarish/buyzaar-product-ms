package com.buyzaar.product.service;

import java.util.List;

import com.buyzaar.product.model.entity.Product;
import com.buyzaar.product.model.entity.Tag;

public interface ProductService {

	List<Tag> getAllTags();

	Tag saveTag(Tag tag);

	void saveProduct(Product product);

	Product getProduct(String productId);

	void updateTagsForProductId(String productId, List<String> tagsIds);

}
