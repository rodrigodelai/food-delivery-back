package com.delai.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delai.model.Banner;
import com.delai.repository.BannerRepository;

@Service
public class BannerService {
	
	@Autowired
	private BannerRepository bannerRepository;
	
	public Banner create(Banner banner) {
		return bannerRepository.save(banner);
	}
	
	public List<Banner> list() {
		return bannerRepository.findAll();
	}
	
	public Banner findById(Long id) {
		return bannerRepository.findById(id).get();
	}
	
	public void delete(Long id) {
		bannerRepository.deleteById(id);
	}
	
}
