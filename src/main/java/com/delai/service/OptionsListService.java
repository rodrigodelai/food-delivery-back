package com.delai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.OptionsList;
import com.delai.repository.OptionsListRepository;

@Service
public class OptionsListService {

	@Autowired
	private OptionsListRepository optionsListRepository;
	
	@Autowired
	private OptionService optionService;
	
	private Logger logger = LoggerFactory.getLogger(OptionsListService.class);
	
	public OptionsList create(OptionsList optionsList) {
		
		// Check if optionsList with the same attributes already exists
		logger.debug("Checking if optionsList already exists...");
		
		var potentialMatches = optionsListRepository.findByName(optionsList.getName());
		
		logger.debug("Potential matches: " + potentialMatches.get().stream().map(pm -> pm.getId()).toList().toString());
		
		if (!potentialMatches.get().isEmpty()) {
			
			var match = potentialMatches.get().stream().filter(ol -> ol.getOptions().equals(optionsList.getOptions())).toList();
			
			if (!match.isEmpty()) {
				logger.debug("OptionsList alredy exists: " + match.get(0).getName());
				
				return match.get(0);
			}
		}
		
		
		// If not, first create its children
		logger.debug("No match found. Creating optionsList...");

		if (optionsList.getOptions() != null) {
			logger.debug("Creating options...");
			
			var options = optionsList.getOptions().stream().map(optionService::create).collect(Collectors.toSet());
			optionsList.setOptions(options);
		}
		
		// Then, save it
		logger.debug("Saving optionsList...");
		
		return optionsListRepository.save(optionsList);
	}
	
	public OptionsList read(Long id) {
		return optionsListRepository.findById(id).get();
	}
	
	public OptionsList update(OptionsList optionsList, Long id) {
		var optionsListFound = optionsListRepository.findById(id);
		
		if (optionsListFound.isEmpty())
			throw new ObjectNotFoundException(id, "OptionsList");
		
		optionsListFound.get().setName(optionsList.getName());
		optionsListFound.get().setOptions(optionsList.getOptions());
		
		return optionsListRepository.save(optionsListFound.get());
	}
	
	public void delete(Long id) {
		// First, delete the associations
		logger.debug("Deleting associations...");
		
		var found = optionsListRepository.findById(id);
		
		if (found.isPresent()) {
			found.get().setOptions(null);
			optionsListRepository.save(found.get());
		}
		
		// Then, delete it
		logger.debug("Deleting optionsList...");
		optionsListRepository.deleteById(id);
	}
	
	public OptionsList addOptions(List<Long> optionsIds, Long optionsListId) {
		var optionsList = optionsListRepository.findById(optionsListId).orElseThrow(() -> new ObjectNotFoundException(optionsListId, "OptionsList"));
		var options = optionsList.getOptions();
		
		optionsIds.forEach(option -> options.add(optionService.read(option)));
		
		return optionsListRepository.save(optionsList);
	}
	
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
	
}
