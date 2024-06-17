package com.delai.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.delai.model.order.Order;
import com.delai.model.order.OrderItem;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o WHERE :orderItem MEMBER OF o.items")
	Optional<List<Order>> findByOrderItem(@Param("orderItem") OrderItem orderItem);

}
