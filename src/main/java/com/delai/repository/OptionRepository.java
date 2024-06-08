package com.delai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delai.model.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {

}
