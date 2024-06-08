package com.delai.service;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.OptionsList;
import com.delai.repository.OptionRepository;
import com.delai.repository.OptionsListRepository;

@Service
public class OptionsListService {

	@Autowired
	private OptionsListRepository optionsListRepository;
	
	@Autowired
	private OptionRepository optionRepository;
	
	public List<OptionsList> list() {
		return optionsListRepository.findAll();
	}
	
	public OptionsList create(OptionsList optionsList) {
		return optionsListRepository.save(optionsList);
	}
	
	public OptionsList findById(Long id) {
		return optionsListRepository.findById(id).get();
	}
	
	public void delete(Long id) {
		optionsListRepository.deleteById(id);
	}
	
	public OptionsList update(OptionsList optionsList, Long id) {
		var optionsListFound = optionsListRepository.findById(id);
		
		if (!optionsListFound.isPresent()) {
			throw new ObjectNotFoundException(id, "OptionsList");
		}
		
		optionsListFound.get().setName(optionsList.getName());
		optionsListFound.get().setOptions(optionsList.getOptions());
		
		return optionsListRepository.save(optionsListFound.get());
	}

	public OptionsList addProducts(List<Long> optionsIds, Long optionsListId) {
		var optionsList = optionsListRepository.findById(optionsListId).get();
		
		optionsIds.forEach(options -> {
			optionsList.getOptions().add(optionRepository.findById(options).get());
		});
		
		return optionsListRepository.save(optionsList);
	}

	public List<OptionsList> createMultiple(List<OptionsList> optionsLists) {
		return optionsListRepository.saveAll(optionsLists);
	}

	public void deleteAll() {
		optionsListRepository.deleteAll();
	}
	
	
}
