package com.buyzaar.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyzaar.product.model.entity.Currency;
import com.buyzaar.product.service.ProductService;

@RestController
@RequestMapping("buyzaar/currency")
public class CurrencyController {
	private ProductService productService;

	public CurrencyController(ProductService productService) {
		this.productService = productService;
	}
	
	@GetMapping
	public ResponseEntity<List<Currency>> getAllCurrencies(){
		return new ResponseEntity<>(productService.getAllCurrencies(),HttpStatus.OK);
	}
	
}
