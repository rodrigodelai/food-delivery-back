package com.delai.service.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.order.OrderOption;
import com.delai.repository.order.OrderOptionRepository;
import com.delai.repository.order.OrderOptionsListRepository;
import com.delai.service.OptionService;

import jakarta.transaction.Transactional;

@Service
public class OrderOptionService {

	@Autowired
	private OrderOptionsListRepository orderOptionsListRepository;
	
	@Autowired
	private OrderOptionRepository orderOptionRepository;
	
	@Autowired
	private OptionService optionService;
	
	public OrderOption create(OrderOption orderOption) {		
		// Check its child
		orderOption.setOption(optionService.read(orderOption.getOption().getId()));
		
		// If it's all good, save it
		orderOption.setId(null);
		return orderOptionRepository.save(orderOption);
	}

	public OrderOption read(Long id) {
		return orderOptionRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	public OrderOption find(OrderOption orderOption) {
		var optionFound = orderOptionRepository.findByOptionAndQuantity(orderOption.getOption(), orderOption.getQuantity()).orElse(new ArrayList<>());
		
		if (!optionFound.isEmpty())
			return optionFound.get(0);			
		
		return null;
	}
	
	public OrderOption update(OrderOption orderOption, Long id) {
		var orderOptionFound = this.read(id);
		
		orderOptionFound.setOption(orderOption.getOption());
		orderOptionFound.setQuantity(orderOption.getQuantity());
		
		return orderOptionRepository.save(orderOptionFound);
	}
	
	public void delete(Long id) {
		var orderOptionFound = orderOptionRepository.findById(id).orElse(null);
		
		if (orderOptionFound != null) {
			// First, delete the associations
			this.removeOrderOptionFromOrderLists(orderOptionFound);
			
			// Then, delete it
			orderOptionRepository.delete(orderOptionFound);
		}
	}
	
	@Transactional
	public List<OrderOption> createMultiple(List<OrderOption> orderOptions) {
		List<OrderOption> saved = new ArrayList<>();
		orderOptions.forEach(orderOption -> saved.add(this.create(orderOption)));
		return saved;
	}
	
	public List<OrderOption> list() {
		return orderOptionRepository.findAll();
	}

	public void deleteMultiple(List<Long> ids) {
		ids.forEach(this::delete);
	}
	
    private void removeOrderOptionFromOrderLists(OrderOption orderOption) {
        var orderOptionsLists = orderOptionsListRepository.findByOrderOption(orderOption).orElse(new ArrayList<>());
        
        for (int i = orderOptionsLists.size(); i > 0; i--) {
        	var orderOptionsList = orderOptionsLists.get(i - 1);
        	orderOptionsList.getOrderOptions().remove(orderOption);
            orderOptionsListRepository.save(orderOptionsList);
        }
    }
	
}
