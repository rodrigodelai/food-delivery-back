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

import com.delai.model.OptionsList;
import com.delai.service.OptionsListService;

@RestController
@RequestMapping("/options-list")
public class OptionsListController {

	@Autowired
	private OptionsListService optionsListService;
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public OptionsList create(@RequestBody OptionsList optionsList) {
		return optionsListService.create(optionsList);
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public OptionsList read(@PathVariable Long id) {
		return optionsListService.read(id);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public OptionsList update(@RequestBody OptionsList optionsList, @PathVariable Long id) {
		return optionsListService.update(optionsList, id);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		optionsListService.delete(id);
	}
	
	@PostMapping("/multiple")
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<OptionsList> createMultiple(@RequestBody List<OptionsList> optionsLists) {
		return optionsListService.createMultiple(optionsLists);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<OptionsList> list() {
		return optionsListService.list();
	}
		
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteMultiple(@RequestBody List<Long> optionsListIds) {
		optionsListService.deleteMultiple(optionsListIds);
	}
	
	@PostMapping("/{id}")
	public OptionsList addOptions(@RequestBody List<Long> optionsIds, @PathVariable(name = "id") Long optionsListId) {
		return optionsListService.addOptions(optionsIds, optionsListId);
	}
	
}
