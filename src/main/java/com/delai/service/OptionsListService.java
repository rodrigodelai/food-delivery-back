package com.delai.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.OptionsList;
import com.delai.repository.OptionsListRepository;
import com.delai.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OptionsListService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OptionsListRepository optionsListRepository;
	
	@Autowired
	private OptionService optionService;
	
	@Transactional
	public OptionsList create(OptionsList optionsList) {
		// Check if its children already exists
		if (optionsList.getOptions() != null) {
			var options = optionsList.getOptions().stream().map(option -> optionService.read(option.getId())).collect(Collectors.toSet()); 
			optionsList.setOptions(options);	
		}
		
		// Check if record with the same attributes already exists
		var optionsListFound = this.find(optionsList);
		
		if (optionsListFound != null)
			throw new RuntimeException("409 (Conflict) - Record already exists: '" + optionsListFound.getName() + " # " + optionsListFound.getId() + "'.");
		
		// If it's all good, save it
		optionsList.setId(null);
		return optionsListRepository.save(optionsList);
	}
	
	public OptionsList read(Long id) {
		return optionsListRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	public OptionsList find(OptionsList optionsList) {	
		var potentialMatches = optionsListRepository.findByName(optionsList.getName()).orElse(new ArrayList<>());
		
		if (!potentialMatches.isEmpty()) {
			var match = potentialMatches.stream().filter(ol -> ol.getOptions().equals(optionsList.getOptions() != null ? optionsList.getOptions() : new HashSet<>())).toList();
			
			if (!match.isEmpty())
				return match.get(0);
		}
		
		return null;
	}
	
	public OptionsList update(OptionsList optionsList, Long id) {
		var optionsListFound = this.read(id);
		optionsListFound.setOptions(null);
		
		// Check if its children already exists
		if (optionsList.getOptions() != null) {
			var options = optionsList.getOptions().stream().map(option -> optionService.read(option.getId())).collect(Collectors.toSet()); 
			optionsListFound.setOptions(options);
		}
		
		// Copy new values
		optionsListFound.setName(optionsList.getName());
		
		// Check if record with the same attributes already exists
		var newOptionsListFound = this.find(optionsListFound);
		
		if (newOptionsListFound != null && newOptionsListFound.getId() != id)
			throw new RuntimeException("409 (Conflict) - Record alredy exists: '" + newOptionsListFound.getName() + " # " + newOptionsListFound.getId() + "'.");
				
		// If it's all good, save it
		return optionsListRepository.save(optionsListFound);
	}
	
	public void delete(Long id) {
		// First, delete the associations
		var optionsList = this.read(id);
		this.removeOptionsFromOptionsList(optionsList);
		this.removeOptionsListFromProducts(optionsList);
		
		// Then, delete it
		optionsListRepository.deleteById(id);
	}

	@Transactional
	public List<OptionsList> createMultiple(List<OptionsList> optionsLists) {
		List<OptionsList> saved = new ArrayList<>();
		optionsLists.forEach(optionsList -> saved.add(create(optionsList)));
		return saved;
	}
	
	public List<OptionsList> list() {
		return optionsListRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> optionsListIds) {
		optionsListIds.forEach(this::delete);
	}	
	
	public OptionsList addOptions(List<Long> optionsIds, Long optionsListId) {
		var optionsList = this.read(optionsListId);
		var options = optionsList.getOptions();
		
		optionsIds.forEach(option -> options.add(optionService.read(option)));
		
		return optionsListRepository.save(optionsList);
	}
	
	private void removeOptionsFromOptionsList(OptionsList optionsList) {
		if (!optionsList.getOptions().isEmpty()) {
			optionsList.setOptions(null);
			optionsListRepository.save(optionsList);			
		}
	}
	
	private void removeOptionsListFromProducts(OptionsList optionsList) {
		var products = productRepository.findByOptionsList(optionsList).orElse(new ArrayList<>());
		
		if (!products.isEmpty()) {
			products.forEach(product -> {
				product.getOptionsLists().remove(optionsList);
				productRepository.save(product);
			});
		}
	}
	
}
