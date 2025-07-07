package com.buyzaar.product.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.buyzaar.product.exceptions.ImageUploadException;
import com.buyzaar.product.model.dto.InputRequest;
import com.buyzaar.product.model.entity.Pricing;
import com.buyzaar.product.model.entity.Product;
import com.buyzaar.product.model.entity.Variant;
import com.buyzaar.product.service.ProductService;

@RestController
@RequestMapping("/buyzaar/products")
public class ProductController {

	ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<String> saveProduct(@RequestBody Product product) {
		productService.saveProduct(product);
		return new ResponseEntity<>("Saved Succesfully", HttpStatus.OK);
	}

	@GetMapping("{productId}")
	public ResponseEntity<Product> getProduct(@PathVariable String productId) {
		return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
	}

	@PatchMapping("{productId}/tags/assign")
	public ResponseEntity<String> assignTagsForProductId(@PathVariable String productId,
			@RequestBody List<String> tagIds) {
		productService.assignTagsForProductId(productId, tagIds);
		return new ResponseEntity<>("Tags updated successfully for the productId :" + productId, HttpStatus.OK);

	}

	@PatchMapping("{productId}/tags/deassign")
	public ResponseEntity<String> deassignTagsForProductId(@PathVariable String productId,
			@RequestBody List<String> tagIds) {
		productService.deassignTagsForProductId(productId, tagIds);
		return new ResponseEntity<>("Tags updated successfully for the productId :" + productId, HttpStatus.OK);

	}

	@PutMapping("{productId}/pricing")
	public ResponseEntity<String> updatePriceForProductId(@PathVariable String productId,
			@RequestBody InputRequest<Pricing> inputRequest) {
		productService.updatePriceForProductId(productId, inputRequest.getRequest());
		return new ResponseEntity<>("Pricing Updated succcesfully", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts(@RequestParam(required = false) String cursorId,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(required = false) Boolean isPrevious,
			@RequestParam(required = false) String query, @RequestParam(required = false) String category,
			@RequestParam(required = false) List<String> tagIds,
			@RequestParam(required = false, defaultValue = "") String minValue,
			@RequestParam(required = false, defaultValue = "") String maxValue,
			@RequestParam(required = false) Boolean hasDiscount, @RequestParam(required = false) Boolean hasStock,
			@RequestParam(required = false) String rating, @RequestParam(defaultValue = "createdAt") String sortBy) {
		boolean goingBackward = Boolean.TRUE.equals(isPrevious);
		boolean applyDiscountFilter = Boolean.TRUE.equals(hasDiscount);
		boolean applyInStockFilter = Boolean.TRUE.equals(hasStock);
		double min = Objects.nonNull(minValue) && !minValue.isBlank() ? Double.parseDouble(minValue) : 0.0;
		double max = Objects.nonNull(maxValue) && !maxValue.isBlank() ? Double.parseDouble(maxValue) : Double.MAX_VALUE;
		double averagerating = Objects.nonNull(rating) && !rating.isBlank() ? Double.parseDouble(rating) : 0.0;
		return new ResponseEntity<>(productService.getAllProducts(cursorId, limit, goingBackward, query, category,
				tagIds, min, max, applyDiscountFilter, applyInStockFilter, averagerating, sortBy), HttpStatus.OK);
	}

	@GetMapping("{productId}/summary")
	public ResponseEntity<Product> getProductSummary(@PathVariable String productId) {
		return new ResponseEntity<>(productService.getProductSummary(productId), HttpStatus.OK);
	}

	@GetMapping("{productId}/related")
	public ResponseEntity<List<Product>> getRelatedProductsByProductId(@PathVariable String productId) {
		return new ResponseEntity<>(productService.getRelatedProductsByProductId(productId), HttpStatus.OK);
	}

	@GetMapping("count")
	public ResponseEntity<Long> getCount() {
		return new ResponseEntity<>(productService.getCount(), HttpStatus.OK);
	}

	@PatchMapping("{productId}/status")
	public ResponseEntity<String> toggleProductStatus(@PathVariable String productId) {
		return new ResponseEntity<>(productService.toggleProductStatus(productId), HttpStatus.OK);
	}

}
