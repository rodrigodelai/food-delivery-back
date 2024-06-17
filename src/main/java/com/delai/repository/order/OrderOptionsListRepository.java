package com.delai.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.delai.model.order.OrderOption;
import com.delai.model.order.OrderOptionsList;

public interface OrderOptionsListRepository extends JpaRepository<OrderOptionsList, Long> {

	public Optional<List<OrderOptionsList>> findByName(String name);
	
	@Query("SELECT ool FROM OrderOptionsList ool WHERE :orderOption MEMBER OF ool.orderOptions")
	Optional<List<OrderOptionsList>> findByOrderOption(@Param("orderOption") OrderOption orderOption);

}
