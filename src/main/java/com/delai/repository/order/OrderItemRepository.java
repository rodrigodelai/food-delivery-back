package com.delai.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delai.model.order.OrderItem;
import com.delai.model.order.OrderOptionsList;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@Query("SELECT oi FROM OrderItem oi WHERE :orderOptionsList MEMBER OF oi.orderOptionsLists")
	Optional<List<OrderItem>> findByOrderOptionsList(OrderOptionsList orderOptionsList);
	
}
