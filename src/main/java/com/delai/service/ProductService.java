package com.delai.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Product;
import com.delai.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	public Product create(Product product) {
		return productRepository.save(product);
	}
	
	public List<Product> list() {
		return productRepository.findAll();
	}
	
	public Product findById(Long id) {
		return productRepository.findById(id).get();
	}
	
	public void delete(Long id) {
	 	productRepository.deleteById(id);
	}
	
}
