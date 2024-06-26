package com.delai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.delai.model.Product;
import com.delai.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Product create(@RequestBody Product product) {
		return productService.create(product);
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Product read(@PathVariable Long id) {
		return productService.read(id);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void update(@RequestBody Product product, @PathVariable Long id) {
		productService.update(product, id);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		productService.delete(id);
	}
		
	@PostMapping("/multiple")
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<Product> createMultiple(@RequestBody List<Product> products) {
		return productService.createMultiple(products);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<Product> list() {
		return productService.list();
	}
	
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteMultiple(@RequestBody List<Long> productIds) {
		productService.deleteMultiple(productIds);
	}
	
	@PostMapping("/{id}")
	public Product addOptionsLists(@RequestBody List<Long> optionsListsIds, @PathVariable(name = "id") Long productId) {
		return productService.addOptionsLists(optionsListsIds, productId);
	}
	
}
