package com.delai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.delai.model.OptionsList;
import com.delai.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	public Optional<List<Product>> findByNameAndDescription(String name, String description);
	
	@Query("SELECT p FROM Product p WHERE :optionsList MEMBER OF p.optionsLists")
	Optional<List<Product>> findByOptionsList(@Param("optionsList") OptionsList optionsList);
	
}
