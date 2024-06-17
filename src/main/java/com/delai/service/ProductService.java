package com.delai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Product;
import com.delai.repository.CategoryRepository;
import com.delai.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OptionsListService optionsListService;
	
	
	@Transactional
	public Product create(Product product) {
		// Check if its children already exists
		if (product.getOptionsLists() != null) {
			var optionsLists = product.getOptionsLists().stream().map(ol -> optionsListService.read(ol.getId())).toList();
			product.setOptionsLists(optionsLists);
		}
				
		// Check if record with the same attributes already exists
		var productFound = this.find(product);
		
		if (productFound != null)
			throw new RuntimeException("409 (Conflict) - Record already exists: '" + productFound.getName() + " # " + productFound.getId() + "'.");
		
		// If it's all good, save it
		product.setId(null);
		return productRepository.save(product);
	}
	
	public Product read(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	public Product find(Product product) {
		var productFound = productRepository.findByNameAndDescription(product.getName(), product.getDescription()).orElse(new ArrayList<>());
		
		if (!productFound.isEmpty())
			return productFound.get(0);
		
		return null;
	}
	
	public Product update(Product product, Long id) {
		var productFound = this.read(id);
		productFound.setOptionsLists(null);
		
		// Check if its children already exists
		if (product.getOptionsLists() != null) {
			var optionsLists = product.getOptionsLists().stream().map(ol -> optionsListService.read(ol.getId())).toList();
			productFound.setOptionsLists(optionsLists);
		}
		
		// Copy new values
		productFound.setName(product.getName());
		productFound.setDescription(product.getDescription());
		productFound.setPrice(product.getPrice());
		productFound.setPromoPrice(product.getPromoPrice());
		
		// Check if record with the same attributes already exists
		var newProductFound = this.find(productFound);
		
		if (newProductFound != null && !newProductFound.getId().equals(id))
			throw new RuntimeException("409 (Conflict) - Record alredy exists: '" + newProductFound.getName() + " # " + newProductFound.getId() + "'.");

		// If it's all good, save it
		return productRepository.save(productFound);
	}	
	
	public void delete(Long id) {
		var productFound = productRepository.findById(id).orElse(null);
		
		if (productFound != null) {
			// First, delete the associations
			this.removeOptionsListsFromProduct(productFound);
			this.removeProductFromCategories(productFound);
			
			// Then, delete it
		 	productRepository.delete(productFound);	
		}
	}

	@Transactional
	public List<Product> createMultiple(List<Product> products) {
		List<Product> saved = new ArrayList<>();
		products.forEach(product -> saved.add(create(product)));
		return saved;
	}

	public List<Product> list() {
		return productRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> productIds) {
		productIds.forEach(this::delete);
	}
	
	public Product addOptionsLists(List<Long> optionsListsIds, Long productId) {
		var product = this.read(productId);
		
		optionsListsIds.forEach(optionsListId -> product.getOptionsLists().add(optionsListService.read(optionsListId)));
		
		return productRepository.save(product);
	}
	
	private void removeOptionsListsFromProduct(Product product) {
		product.setOptionsLists(null);
		productRepository.save(product);
	}
	
	private void removeProductFromCategories(Product product) {
		var categories = categoryRepository.findByProduct(product).orElse(new ArrayList<>());

		if (!categories.isEmpty()) {
			categories.forEach(category -> {
				category.getProducts().remove(product);
				categoryRepository.save(category);
			});			
		}	
	}

}
