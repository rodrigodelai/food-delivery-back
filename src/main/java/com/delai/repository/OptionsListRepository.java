package com.delai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.delai.model.Option;
import com.delai.model.OptionsList;

public interface OptionsListRepository extends JpaRepository<OptionsList, Long> {

	public Optional<List<OptionsList>> findByName(String name);
	
	@Query("SELECT ol FROM OptionsList ol WHERE :option MEMBER OF ol.options")
	Optional<List<OptionsList>> findByOption(@Param("option") Option option);

}
