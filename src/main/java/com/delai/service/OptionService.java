package com.delai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Option;
import com.delai.model.OptionsList;
import com.delai.repository.OptionRepository;
import com.delai.repository.OptionsListRepository;

import jakarta.transaction.Transactional;

@Service
public class OptionService {

	@Autowired
	private OptionsListRepository optionsListRepository;
	
	@Autowired
	private OptionRepository optionRepository;
	
	@Transactional
	public Option create(Option option) {
		// Check if record with the same attributes already exists
		var optionFound = this.find(option);
		
		if (optionFound != null)
			throw new RuntimeException("409 (Conflict) - Record already exists: " + optionFound.getName() + ".");
			
		// If it's all good, save it
		option.setId(null);
		return optionRepository.save(option);
	}

	public Option read(Long id) {
		return optionRepository.findById(id).orElseThrow(() -> new RuntimeException("404 (NotFound) - Record with the given ID was not found: '# " + id + "'."));
	}
	
	public Option find(Option option) {
		var optionFound = optionRepository.findByNameAndPriceAndImageName(option.getName(), option.getPrice(), option.getImageName()).orElse(new ArrayList<>());
		
		if (!optionFound.isEmpty())
			return optionFound.get(0);			
		
		return null;
	}
	
	public Option update(Option option, Long id) {
		var optionFound = this.read(id);
		
		optionFound.setName(option.getName());
		optionFound.setPrice(option.getPrice());
		optionFound.setImageName(option.getImageName());
		
		return optionRepository.save(optionFound);
	}
	
	public void delete(Long id) {
		// First, delete the associations
		var option = this.read(id);
		this.removeOptionFromLists(option);
		
		// Then, delete it
		optionRepository.deleteById(id);
	}
	
	@Transactional
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
        var optionsLists = optionsListRepository.findByOption(option).orElse(new ArrayList<>());

        for (OptionsList optionsList : optionsLists) {
            optionsList.getOptions().remove(option);
            optionsListRepository.save(optionsList);
        }
    }
	
}
