package com.delai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delai.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	public Optional<List<Product>> findByNameAndDescription(String name, String description);
	
}
