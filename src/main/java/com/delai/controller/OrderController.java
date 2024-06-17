package com.delai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.delai.model.order.Order;
import com.delai.service.order.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Order create(@RequestBody Order order) {
		return orderService.create(order);
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Order read(@PathVariable Long id) {
		return orderService.read(id);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Order update(@RequestBody Order order, @PathVariable Long id) {
		return orderService.update(order, id);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		orderService.delete(id);
	}
	
	@PostMapping("/multiple")
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<Order> createMultiple(@RequestBody List<Order> orders) {
		return orderService.createMultiple(orders);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<Order> list() {
		return orderService.list();
	}
	
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteMultiple(@RequestBody List<Long> orderIds) {
		orderService.deleteMultiple(orderIds);
	}
	
}
