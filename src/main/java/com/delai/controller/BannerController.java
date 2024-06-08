package com.delai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.delai.model.Banner;
import com.delai.service.BannerService;

@RestController
@RequestMapping("/banner")
public class BannerController {

	@Autowired
	private BannerService bannerService;
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Banner create(@RequestBody Banner banner) {
		return bannerService.create(banner);
	}
	
	@PostMapping("/multiple")
	@ResponseStatus(code = HttpStatus.CREATED)
	public List<Banner> createMultiple(@RequestBody List<Banner> banners) {
		return bannerService.createMultiple(banners);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<Banner> list() {
		return bannerService.list();
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Banner findById(@PathVariable Long id) {
		return bannerService.findById(id);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		bannerService.delete(id);
	}
	
}
