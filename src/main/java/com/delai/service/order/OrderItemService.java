package com.delai.service.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.order.OrderItem;
import com.delai.model.order.OrderOptionsList;
import com.delai.repository.order.OrderItemRepository;
import com.delai.repository.order.OrderRepository;
import com.delai.service.ProductService;

import jakarta.transaction.Transactional;

@Service
public class OrderItemService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderOptionsListService orderOptionsListService;
	
	@Transactional
	public OrderItem create(OrderItem orderItem) {
		// First, check its children
		orderItem.setProduct(productService.read(orderItem.getProduct().getId()));
		
		if (orderItem.getOrderOptionsLists() != null) {
			var orderOptionsLists = new ArrayList<OrderOptionsList>();
			
			orderItem.getOrderOptionsLists().forEach(ool -> {
				var orderOptionsList = orderOptionsListService.create(ool);
				
				if (orderOptionsListService.containsOptionList(orderItem.getProduct(), orderOptionsList))
					orderOptionsLists.add(orderOptionsList);
				else 
					throw new RuntimeException("400 (BadRequest) - Some of the options selected do not belong to the product. Verify: '" + orderOptionsList.getName() + " # " + orderOptionsList.getId() + "'.");
			});
			
			orderItem.setOrderOptionsLists(orderOptionsLists);
		}
		
		// Send after to DTO
		orderItem.setPrice(orderItem.getPrice().setScale(2, RoundingMode.HALF_UP));
		
		// Check if price was correctly calculated
		if (!this.calculatePrice(orderItem).equals(orderItem.getPrice()))
			throw new RuntimeException("400 (BadRequest) - Price was not calculated correctly. Correct value is: R$ " + this.calculatePrice(orderItem) + ".");
		
		// Then, save it
		return orderItemRepository.save(orderItem);
	}

	public OrderItem read(Long id) {
		return orderItemRepository.findById(id).orElseThrow();
	}
	
	public void delete(Long id) {
		var orderItemFound = orderItemRepository.findById(id).orElse(null);
		
		if (orderItemFound != null) {
			// First, delete the associations
			this.removeProductFromOrderItem(orderItemFound);
			this.removeAndDeleteOrderOptionsListFromOrderItem(orderItemFound);
			this.removeOrderItemFromOrder(orderItemFound);
			
			// Then, delete it
			orderItemRepository.delete(orderItemFound);
		}
	}

	private void removeAndDeleteOrderOptionsListFromOrderItem(OrderItem orderItem) {
		if (orderItem.getOrderOptionsLists() != null) {
			var orderOptionsListsIds = orderItem.getOrderOptionsLists().stream()
					.map(OrderOptionsList::getId)
					.toList();
			
			orderOptionsListsIds.forEach(orderOptionsListService::delete);
		}
	}

	private void removeOrderItemFromOrder(OrderItem orderItem) {
		var orders = orderRepository.findByOrderItem(orderItem).orElse(new ArrayList<>());
		
		orders.forEach(order -> {
			order.getItems().remove(orderItem);
			orderRepository.save(order);
		});
	}

	private void removeProductFromOrderItem(OrderItem orderItem) {
		orderItem.setProduct(null);
		orderItemRepository.save(orderItem);
	}

	private BigDecimal calculatePrice(OrderItem orderItem) {
		var productPrice = (orderItem.getProduct().getPromoPrice() != null ?
				orderItem.getProduct().getPromoPrice() : 
				orderItem.getProduct().getPrice());
		
		if (orderItem.getOrderOptionsLists() == null)
			return productPrice;
		
		var optionalsPrice = orderItem.getOrderOptionsLists().stream()
				.map(ool -> ool.getOrderOptions().stream()
							.map(orderOption -> orderOption.getOption().getPrice().multiply(new BigDecimal(orderOption.getQuantity())))
							.reduce(BigDecimal.ZERO, BigDecimal::add))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return productPrice.add(optionalsPrice);
	}
	
}
