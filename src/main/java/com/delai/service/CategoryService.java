package com.delai.service;

import java.util.List;

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
	
	public List<Category> list(Boolean projection) {
		
		if (projection)
			return categoryRepository.findAllCustom()
					.stream()
					.map(category -> new Category(category.getId(), category.getName(), null))
					.toList();
		
		return categoryRepository.findAll();
	}
	
	public Category findById(Long id) {
		return categoryRepository.findById(id).get();
	}
	
	public void delete(Long id) {
		categoryRepository.deleteById(id);
	}

	public Category addProducts(List<Long> productIds, Long categoryId) {
		var category = categoryRepository.findById(categoryId).get();
		
		productIds.forEach(product -> {
			category.getProducts().add(productRepository.findById(product).get());
		});
		
		return categoryRepository.save(category);
		
	}

	public List<Category> createMultiple(List<Category> categories) {
		return categoryRepository.saveAll(categories);
	}

	public void deleteAll() {
		categoryRepository.deleteAll();
	}
	
}
