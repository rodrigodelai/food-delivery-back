package com.delai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Category;
import com.delai.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductService productService;
	
	private Logger logger = LoggerFactory.getLogger(CategoryService.class);
	
	public Category create(Category category) {
		// Check if category with the same attributes already exists
		logger.debug("Checking if category already exists...");
		
		var potentialMatches = categoryRepository.findByName(category.getName());
		logger.debug("Potential matches: " + potentialMatches.get().stream().map(pm -> pm.getId()).toList().toString());
		
		if (!potentialMatches.get().isEmpty()) {
			var match = potentialMatches.get().stream().filter(cat -> cat.getProducts().equals(category.getProducts())).toList();
			
			if (!match.isEmpty()) {
				logger.debug("Category alredy exists: " + match.get(0).getName());

				return match.get(0);
			}
		}
		
		// If not, first create its children
		logger.debug("No match found. Creating category...");
		
		if (category.getProducts() != null) {
			logger.debug("Creating products...");
			
			var products = category.getProducts().stream().map(productService::create).collect(Collectors.toSet());
			category.setProducts(products);
		}
		
		// Then, save it
		logger.debug("Saving category...");
		
		return categoryRepository.save(category);
	}
	
	public Category read(Long id) {
		return categoryRepository.findById(id).get();
	}
	
	public Category update(Category category, Long id) {
		var categoryFound = categoryRepository.findById(id);
		
		if (!categoryFound.isPresent()) {
			throw new ObjectNotFoundException(id, "Category");
		}
		
		categoryFound.get().setName(category.getName());
		categoryFound.get().setProducts(category.getProducts());
		
		return categoryRepository.save(categoryFound.get());
	}
	
	public void delete(Long id) {
		// First, delete the associations
		logger.debug("Deleting associations...");
		
		var found = categoryRepository.findById(id);
		
		if (found.isPresent()) {
			found.get().setProducts(null);
			categoryRepository.save(found.get());
		}
		
		// Then, delete it
		logger.debug("Deleting category...");
		categoryRepository.deleteById(id);
	}
	
	public Category addProducts(List<Long> productIds, Long categoryId) {
		var category = categoryRepository.findById(categoryId).orElseThrow(() -> new ObjectNotFoundException(categoryId, "Category"));
		var products = category.getProducts();
		
		productIds.forEach(id -> {
			products.add(productService.read(id));
		});
		
		return categoryRepository.save(category);
	}
	
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
	
}
