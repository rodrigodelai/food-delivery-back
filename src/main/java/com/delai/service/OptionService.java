package com.delai.service;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Option;
import com.delai.repository.OptionRepository;

@Service
public class OptionService {

	@Autowired
	private OptionRepository optionRepository;
	
	public Option create(Option option) {
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
		optionRepository.deleteById(id);
	}
	
	public List<Option> createMultiple(List<Option> options) {
		return optionRepository.saveAll(options);
	}
	
	public List<Option> list() {
		return optionRepository.findAll();
	}

	public void deleteMultiple(List<Long> ids) {
		optionRepository.deleteAllById(ids);
	}
	
}
