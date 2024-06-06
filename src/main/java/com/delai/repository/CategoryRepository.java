package com.delai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delai.model.Category;
import com.delai.projection.CategoryProjection;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
    @Query("SELECT c.id as id, c.name as name FROM Category c")
	public List<CategoryProjection> findAllCustom();
	
}
