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

import com.delai.model.Option;
import com.delai.service.OptionService;

@RestController
@RequestMapping("option")
public class OptionController {

	@Autowired
	private OptionService optionService;
	
	@PostMapping()
	public Option create(@RequestBody Option option) {
		return optionService.create(option);
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Option read(@PathVariable Long id) {
		return optionService.read(id);
	}
	
	@PutMapping("/{id}")
	public Option update(@RequestBody Option option, @PathVariable Long id) {
		return optionService.update(option, id);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		optionService.delete(id);
	}
	
	@PostMapping("/multiple")
	public List<Option> createMultiple(@RequestBody List<Option> options) {
		return optionService.createMultiple(options);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<Option> list() {
		return optionService.list();
	}
	
	@DeleteMapping
	public void deleteMultiple(@RequestBody List<Long> optionIds) {
		optionService.deleteMultiple(optionIds);
	}
	
}
