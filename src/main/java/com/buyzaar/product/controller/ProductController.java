package com.buyzaar.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyzaar.product.model.entity.Product;
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
	public ResponseEntity<Product> getProduct(@PathVariable String productId){
		return new ResponseEntity<>(productService.getProduct(productId),HttpStatus.OK);
	}
	
	@PatchMapping("{productId}/tags/assign")
	public ResponseEntity<String> assignTagsForProductId(@PathVariable String productId,@RequestBody List<String> tagIds){
		productService.assignTagsForProductId(productId,tagIds);
		return new ResponseEntity<>("Tags updated successfully for the productId :"+productId,HttpStatus.OK);
		
	}
	
	@PatchMapping("{productId}/tags/deassign")
	public ResponseEntity<String> deassignTagsForProductId(@PathVariable String productId,@RequestBody List<String> tagIds){
		productService.deassignTagsForProductId(productId,tagIds);
		return new ResponseEntity<>("Tags updated successfully for the productId :"+productId,HttpStatus.OK);
		
	}
	
	
}
