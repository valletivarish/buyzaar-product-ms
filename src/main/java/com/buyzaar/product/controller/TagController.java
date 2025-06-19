package com.buyzaar.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyzaar.product.model.dto.InputRequest;
import com.buyzaar.product.model.dto.TagDTO;
import com.buyzaar.product.model.entity.Tag;
import com.buyzaar.product.service.ProductService;
import com.buyzaar.product.utils.DtoConverter;

@RestController
@RequestMapping("/buyzaar/products/tags")
public class TagController {
	ProductService productService;

	public TagController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<Tag>> getAllTags() {
		List<Tag> tags = productService.getAllTags();
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Tag> saveTag(@RequestBody InputRequest<TagDTO> inputRequest){
		TagDTO tagDto = inputRequest.getRequest();
		Tag savedTag = productService.saveTag(DtoConverter.convertTagDtoToTag(tagDto));
		return new ResponseEntity<>(savedTag,HttpStatus.OK);
	}
	
}
