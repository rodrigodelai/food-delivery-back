package com.delai.service;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Category;
import com.delai.repository.CategoryRepository;
import com.delai.repository.ProductRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	public Category create(Category category) {
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
		categoryRepository.deleteById(id);
	}
	
	public List<Category> createMultiple(List<Category> categories) {
		return categoryRepository.saveAll(categories);
	}
	
	public List<Category> list() {	
		return categoryRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> categoryIds) {
		categoryRepository.deleteAllById(categoryIds);
	}
	
	public Category addProducts(List<Long> productIds, Long categoryId) {
		var category = categoryRepository.findById(categoryId).orElseThrow();
		var products = category.getProducts();
		
		productIds.forEach(product -> {
			products.add(productRepository.findById(product).get());
		});
		
		return categoryRepository.save(category);
	}
	
}
