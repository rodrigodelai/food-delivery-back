package com.delai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delai.model.Category;
import com.delai.model.Product;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	public Optional<List<Category>> findByName(String name);
	
	@Query("SELECT c FROM Category c WHERE :product MEMBER OF c.products")
	public Optional<List<Category>> findByProduct(Product product);
	
}
