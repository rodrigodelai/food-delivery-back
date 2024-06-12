package com.delai.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Product;
import com.delai.repository.CategoryRepository;
import com.delai.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OptionsListService optionsListService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	private Logger logger = LoggerFactory.getLogger(ProductService.class);
	
	public Product create(Product product) {		
		// Check if Product with the same attributes already exists
		logger.debug("Checking if product already exists...");
		
		var productFound = productRepository.findByNameAndDescription(product.getName(), product.getDescription());
		
		if (!productFound.get().isEmpty()) {
			logger.debug("Product alredy exists: " + productFound.get().get(0).getName());
			
			return productFound.get().get(0);
		}
		
		// If not, first create its children
		logger.debug("No match found. Creating product...");
		
		if (product.getOptionsLists() != null) {
			logger.debug("Creating options lists...");
			
			var optionsLists = product.getOptionsLists().stream().map(optionsListService::create).toList();
			product.setOptionsLists(optionsLists);			
		}
		
		// Then, save it
		logger.debug("Saving product...");
		
		return productRepository.save(product);
	}
	
	public Product read(Long id) {
		return productRepository.findById(id).orElseThrow();
	}
	
	public Product update(Product product, Long id) {
		var productFound = productRepository.findById(id);
		
		if (productFound.isEmpty())
			throw new ObjectNotFoundException(id, "Product");
		
		productFound.get().setName(product.getName());
		productFound.get().setDescription(product.getDescription());
		productFound.get().setPrice(product.getPrice());
		productFound.get().setPromoPrice(product.getPromoPrice());
		productFound.get().setOptionsLists(product.getOptionsLists());
		
		return productRepository.save(productFound.get());
	}	
	
	public void delete(Long id) {
		// First, delete the associations
		logger.debug("Deleting associations...");
		
		var productFound = read(id);
		removeOptionsListsFromProduct(productFound);
		removeProductFromCategories(productFound);
		
		// Then, delete it
		logger.debug("Deleting product...");
		
	 	productRepository.delete(productFound);
	}

	public Product addOptionsLists(List<Long> optionsListsIds, Long productId) {
		var product = productRepository.findById(productId).orElseThrow(() -> new ObjectNotFoundException(productId, "Product"));
		
		optionsListsIds.forEach(optionsListId -> {
			product.getOptionsLists().add(optionsListService.read(optionsListId));
		});
		
		return productRepository.save(product);
	}

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
	
	private void removeOptionsListsFromProduct(Product product) {
		product.setOptionsLists(null);
		productRepository.save(product);
	}
	
	private void removeProductFromCategories(Product productFound) {
		var categories = categoryRepository.findByProduct(productFound);

		if (categories.isPresent()) {
			categories.get().forEach(category -> {
				category.getProducts().remove(productFound);
				categoryRepository.save(category);
			});			
		}	
	}

}
