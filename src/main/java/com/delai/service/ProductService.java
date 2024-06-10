package com.delai.service;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Product;
import com.delai.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OptionsListService optionsListService;
	
	public Product create(Product product) {
		return productRepository.save(product);
	}
	
	public List<Product> list() {
		return productRepository.findAll();
	}
	
	public Product read(Long id) {
		return productRepository.findById(id).orElseThrow();
	}
	
	public void delete(Long id) {
	 	productRepository.deleteById(id);
	}

	public Product update(Product product, Long id) {
		var productFound = productRepository.findById(id);
		
		if (!productFound.isPresent()) {
			throw new ObjectNotFoundException(id, "Product");
		}
		
		productFound.get().setName(product.getName());
		productFound.get().setDescription(product.getDescription());
		productFound.get().setPrice(product.getPrice());
		productFound.get().setPromoPrice(product.getPromoPrice());
		productFound.get().setOptionsLists(product.getOptionsLists());
		
		return productRepository.save(productFound.get());
	}

	public Product addOptionsLists(List<Long> optionsListsIds, Long productId) {
		var product = productRepository.findById(productId).get();
		
		optionsListsIds.forEach(optionsListId -> {
			product.getOptionsLists().add(optionsListService.read(optionsListId));
		});
		
		return productRepository.save(product);
	}

	public List<Product> createMultiple(List<Product> products) {
		return productRepository.saveAll(products);
	}

	public void deleteAll() {
		productRepository.deleteAll();
	}

	public void deleteMultiple(List<Long> productIds) {
		productRepository.deleteAllById(productIds);
	}

}
