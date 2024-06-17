package com.delai.service.order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Product;
import com.delai.model.order.OrderOption;
import com.delai.model.order.OrderOptionsList;
import com.delai.repository.order.OrderItemRepository;
import com.delai.repository.order.OrderOptionsListRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderOptionsListService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private OrderOptionsListRepository orderOptionsListRepository;
	
	@Autowired
	private OrderOptionService orderOptionService;
	
	@Transactional
	public OrderOptionsList create(OrderOptionsList orderOptionsList) {
		// Create its children
		if (orderOptionsList.getOrderOptions() != null) {
			orderOptionsList.setOrderOptions(orderOptionsList.getOrderOptions().stream()
				.map(orderOptionService::create)
				.collect(Collectors.toSet()));
		}
		
		// If it's all good, save it
		orderOptionsList.setId(null);
		return orderOptionsListRepository.save(orderOptionsList);
	}
	
	public OrderOptionsList read(Long id) {
		return orderOptionsListRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	public OrderOptionsList find(OrderOptionsList orderOptionsList) {	
		var potentialMatches = orderOptionsListRepository.findByName(orderOptionsList.getName()).orElse(new ArrayList<>());
		
		if (!potentialMatches.isEmpty()) {
			var match = potentialMatches.stream()
					.filter(ool -> ool.getOrderOptions().equals(orderOptionsList.getOrderOptions() != null ?
							orderOptionsList.getOrderOptions() :
							new HashSet<>()))
					.toList();
			
			if (!match.isEmpty())
				return match.get(0);
		}
		
		return null;
	}
	
	public OrderOptionsList update(OrderOptionsList orderOptionsList, Long id) {
		var orderOptionsListFound = this.read(id);
		orderOptionsListFound.getOrderOptions().forEach(orderOption -> orderOptionService.delete(orderOption.getId()));
		
		// Create its children
		if (orderOptionsList.getOrderOptions() != null) {
			orderOptionsListFound.setOrderOptions(orderOptionsList.getOrderOptions().stream()
				.map(orderOptionService::create)
				.collect(Collectors.toSet()));
		}
		
		// Copy new values
		orderOptionsListFound.setName(orderOptionsList.getName());
				
		// If it's all good, save it
		return orderOptionsListRepository.save(orderOptionsListFound);
	}
	
	public void delete(Long id) {
		var orderOptionsListFound = orderOptionsListRepository.findById(id).orElse(null);
		
		if (orderOptionsListFound != null) {
			// First, delete the associations
			this.removeAndDeleteOrderOptionsFromOrderOptionsList(orderOptionsListFound);
			this.removeOrderOptionsListFromOrderItems(orderOptionsListFound);
			
			// Then, delete it
			orderOptionsListRepository.delete(orderOptionsListFound);
		}
	}

	@Transactional
	public List<OrderOptionsList> createMultiple(List<OrderOptionsList> orderOptionsLists) {
		List<OrderOptionsList> saved = new ArrayList<>();
		orderOptionsLists.forEach(orderOptionsList -> saved.add(create(orderOptionsList)));
		return saved;
	}
	
	public List<OrderOptionsList> list() {
		return orderOptionsListRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> optionsListIds) {
		optionsListIds.forEach(this::delete);
	}	
	
	public OrderOptionsList addOrderOptions(List<Long> orderOptionsIds, Long orderOptionsListId) {
		var orderOptionsList = this.read(orderOptionsListId);
		var orderOptions = orderOptionsList.getOrderOptions();
		
		orderOptionsIds.forEach(orderOption -> orderOptions.add(orderOptionService.read(orderOption)));
		
		return orderOptionsListRepository.save(orderOptionsList);
	}
	
	private void removeAndDeleteOrderOptionsFromOrderOptionsList(OrderOptionsList orderOptionsList) {
		var orderOptionsIds = orderOptionsList.getOrderOptions().stream()
				.map(OrderOption::getId)
				.toList();
		
		orderOptionsIds.forEach(orderOptionService::delete);
	}
	
	private void removeOrderOptionsListFromOrderItems(OrderOptionsList orderOptionsList) {
		var orderItems = orderItemRepository.findByOrderOptionsList(orderOptionsList).orElse(new ArrayList<>());
		
		orderItems.forEach(orderItem -> {
			orderItem.setOrderOptionsLists(null);
			orderItemRepository.save(orderItem);
		});
	}

	public boolean containsOptionList(Product product, OrderOptionsList orderOptionsList) {
		var optionsLists = product.getOptionsLists();
		
		var found = optionsLists.stream()
				.filter(ol -> ol.getName().equals(orderOptionsList.getName()) &&
					ol.getOptions().containsAll(orderOptionsList.getOrderOptions().stream()
						.map(oo -> oo.getOption())
						.collect(Collectors.toSet())))
				.toList();
		
		return !found.isEmpty();
	}
	
}
