package com.delai.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delai.model.Option;
import com.delai.model.order.OrderOption;

public interface OrderOptionRepository extends JpaRepository<OrderOption, Long> {

	public Optional<List<OrderOption>> findByOptionAndQuantity(Option option, Integer quantity);
	
}
