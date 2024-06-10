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

import com.delai.model.Category;
import com.delai.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Category create(@RequestBody Category category) {
		return categoryService.create(category);
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Category read(@PathVariable Long id) {
		return categoryService.read(id);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Category update(@RequestBody Category category, @PathVariable Long id) {
		return categoryService.update(category, id);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		categoryService.delete(id);
	}
		
	@PostMapping("/multiple")
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<Category> createMultiple(@RequestBody List<Category> categories) {
		return categoryService.createMultiple(categories);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<Category> list() {
		return categoryService.list();
	}
	
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteMultiple(@RequestBody List<Long> categoryIds) {
		categoryService.deleteMultiple(categoryIds);
	}
	
	@PostMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Category addProducts(@RequestBody List<Long> productIds, @PathVariable(name = "id") Long categoryId) {
		return categoryService.addProducts(productIds, categoryId);
	}
	
}
