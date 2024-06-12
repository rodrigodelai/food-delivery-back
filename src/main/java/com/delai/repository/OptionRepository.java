package com.delai.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delai.model.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {

	public Optional<List<Option>> findByNameAndPriceAndImageName(String name, BigDecimal price, String imageName);
	
}
