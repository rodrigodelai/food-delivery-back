package com.delai.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Category;
import com.delai.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductService productService;
	
	@Transactional
	public Category create(Category category) {
		// Check if its children already exists
		if (category.getProducts() != null) {
			var products = category.getProducts().stream().map(p -> productService.read(p.getId())).collect(Collectors.toSet());
			category.setProducts(products);
		}
		
		// Check if category with the same attributes already exists
		var categoryFound = this.find(category);
		
		if (categoryFound != null)
			throw new RuntimeException("409 (Conflict) - Record already exists: '" + categoryFound.getName() + " # " + categoryFound.getId() + "'.");
		
		// If it's all good, save it
		category.setId(null);
		return categoryRepository.save(category);
	}
	
	public Category read(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	private Category find(Category category) {
		var potentialMatches = categoryRepository.findByName(category.getName()).orElse(new ArrayList<>());
		
		if (!potentialMatches.isEmpty()) {
			var match = potentialMatches.stream().filter(cat -> cat.getProducts().equals(category.getProducts() != null ? category.getProducts() : new HashSet<>())).toList();
			
			if (!match.isEmpty()) 
				return match.get(0);
		}

		return null;
	}
	
	public Category update(Category category, Long id) {
		var categoryFound = this.read(id);
		categoryFound.setProducts(null);
		
		// Check if its children already exists
		if (category.getProducts() != null) {
			var products = category.getProducts().stream().map(p -> productService.read(p.getId())).collect(Collectors.toSet());
			categoryFound.setProducts(products);
		}
		
		// Copy new values
		categoryFound.setName(category.getName());
		
		// Check if record with the same attributes already exists
		var newCategoryFound = this.find(categoryFound);
		
		if (newCategoryFound != null)
			throw new RuntimeException("409 (Conflict) - Record already exists: '" + newCategoryFound.getName() + " # " + newCategoryFound.getId() + "'.");
		
		// If it's all good, save it
		return categoryRepository.save(categoryFound);
	}
	
	public void delete(Long id) {
		var categoryFound = categoryRepository.findById(id).orElse(null);
		
		if (categoryFound != null) {
			// First, delete the associations
			this.removeProductsFromCategory(categoryFound);
			
			// Then, delete it
			categoryRepository.delete(categoryFound);
		}
	}
	
	@Transactional
	public List<Category> createMultiple(List<Category> categories) {
		List<Category> saved = new ArrayList<>();
		categories.forEach(category -> saved.add(create(category)));
		return saved;
	}
	
	public List<Category> list() {	
		return categoryRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> categoryIds) {
		categoryIds.forEach(this::delete);
	}
	
	public Category addProducts(List<Long> productIds, Long categoryId) {
		var category = this.read(categoryId);
		var products = category.getProducts();
		
		productIds.forEach(id -> products.add(productService.read(id)));
		
		return categoryRepository.save(category);
	}
	
	private void removeProductsFromCategory(Category category) {
		category.setProducts(null);
		categoryRepository.save(category);
	}
	
}
