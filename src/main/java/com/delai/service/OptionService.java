package com.delai.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Option;
import com.delai.model.OptionsList;
import com.delai.repository.OptionRepository;
import com.delai.repository.OptionsListRepository;

@Service
public class OptionService {

	@Autowired
	private OptionRepository optionRepository;
	
	@Autowired
	private OptionsListRepository optionsListRepository;
	
	private Logger logger = LoggerFactory.getLogger(OptionService.class);
	
	public Option create(Option option) {
		
		// Check if option with the same attributes already exists
		logger.debug("Checking if option already exists...");
		
		var optionFound = optionRepository.findByNameAndPriceAndImageName(option.getName(), option.getPrice(), option.getImageName());
		
		if (!optionFound.get().isEmpty()) {
			logger.debug("Option alredy exists: " + optionFound.get().get(0).getName());
			
			return optionFound.get().get(0);			
		}
		
		// If not, create it
		logger.debug("No match found. Saving option...");
		
		return optionRepository.save(option);
	}

	public Option read(Long id) {
		return optionRepository.findById(id).orElseThrow();
	}
	
	public Option update(Option option, Long id) {
		var optionFound = optionRepository.findById(id);
		
		if (!optionFound.isPresent()) {
			throw new ObjectNotFoundException(id, "Option");
		}
		
		optionFound.get().setName(option.getName());
		optionFound.get().setPrice(option.getPrice());
		optionFound.get().setImageName(option.getImageName());
		
		return optionRepository.save(optionFound.get());
	}
	
	public void delete(Long id) {
		// First, delete the associations
		logger.debug("Deleting associations...");
		
		removeOptionFromLists(read(id));
		
		// Then, delete it
		logger.debug("Deleting option...");
		
		optionRepository.deleteById(id);
	}
	
	public List<Option> createMultiple(List<Option> options) {
		List<Option> saved = new ArrayList<>();
		options.forEach(option -> saved.add(create(option)));
		return saved;
	}
	
	public List<Option> list() {
		return optionRepository.findAll();
	}

	public void deleteMultiple(List<Long> ids) {
		ids.forEach(this::delete);
	}
	
    private void removeOptionFromLists(Option option) {
        var optionsLists = optionsListRepository.findByOption(option).orElseThrow(() -> new ObjectNotFoundException(option.getId(), "OptionsList"));

        for (OptionsList optionsList : optionsLists) {
            optionsList.getOptions().remove(option);
            optionsListRepository.save(optionsList);
        }
    }
	
}
