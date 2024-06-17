package com.delai.service.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.order.Order;
import com.delai.model.order.OrderItem;
import com.delai.repository.order.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemService orderItemService;
	
	@Transactional
	public Order create(Order order) {	
		// Create its children
		var items = order.getItems().stream().map(item -> orderItemService.create(item)).toList();
		order.setItems(items);
		
		// Send after to DTO
		order.setSubtotal(order.getSubtotal().setScale(2, RoundingMode.HALF_UP));
		order.setDeliveryFee(order.getDeliveryFee().setScale(2, RoundingMode.HALF_UP));
		order.setTaxes(order.getTaxes().setScale(2, RoundingMode.HALF_UP));
		
		// Check if subtotal was correctly calculated
		if (!this.calculateSubtotal(order).equals(order.getSubtotal()))
			throw new RuntimeException("409 (Conflict) - Subtotal was not calculated correctly. Correct value: R$ " + this.calculateSubtotal(order) + ".");
		
		// Then, save it
		order.setId(null);
		return orderRepository.save(order);
	}

	public Order read(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	public Order update(Order order, Long id) {
		var orderFound = this.read(id);
		orderFound.getItems().forEach(item -> orderItemService.delete(item.getId()));
		
		// Create its children
		var items = order.getItems().stream().map(item -> orderItemService.create(item)).toList();
		order.setItems(items);
		
		// Send after to DTO
		order.setSubtotal(order.getSubtotal().setScale(2, RoundingMode.HALF_UP));
		order.setDeliveryFee(order.getDeliveryFee().setScale(2, RoundingMode.HALF_UP));
		order.setTaxes(order.getTaxes().setScale(2, RoundingMode.HALF_UP));
		
		// Check if subtotal was correctly calculated
		if (!this.calculateSubtotal(order).equals(order.getSubtotal()))
			throw new RuntimeException("409 (Conflict) - Subtotal was not calculated correctly. Correct value: R$ " + this.calculateSubtotal(order) + ".");
		
		// Copy the new values
		orderFound.setSubtotal(order.getSubtotal());
		orderFound.setDeliveryFee(order.getDeliveryFee());
		orderFound.setTaxes(order.getTaxes());
		orderFound.getItems().addAll(order.getItems());
		
		// Check if subtotal was correctly calculated
		if (!this.calculateSubtotal(orderFound).equals(orderFound.getSubtotal()))
			throw new RuntimeException("409 (Conflict) - Subtotal was not calculated correctly. Correct value: R$ " + this.calculateSubtotal(orderFound) + ".");
		
		// Then, save it
		orderFound.setUpdatedAt(LocalDateTime.now());
		return orderRepository.save(orderFound);
	}
	
	public void delete(Long id) {
		var orderFound = orderRepository.findById(id).orElse(null);
		
		if (orderFound != null) {
			// First, delete the associations
			this.removeAndDeleteOrderItemsFromOrder(orderFound);
			
			// Then, delete it
			orderRepository.delete(orderFound);			
		}
	}

	@Transactional
	public List<Order> createMultiple(List<Order> orders) {
		var saved = new ArrayList<Order>();
		orders.forEach(order -> saved.add(create(order)));
		return saved;
	}
	
	public List<Order> list() {
		return orderRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> orderIds) {
		orderIds.forEach(this::delete);
	}
	
	private void removeAndDeleteOrderItemsFromOrder(Order order) {
		var orderItemsIds = order.getItems().stream()
				.map(OrderItem::getId)
				.toList();
		
		orderItemsIds.forEach(orderItemService::delete);
	}

	private BigDecimal calculateSubtotal(Order order) {
		return order.getItems().stream()
				.map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
}
