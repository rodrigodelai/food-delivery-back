package com.delai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delai.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {	
	
	Optional<Image> findByName(String name);
	
	void deleteByName(String name);
	
}
