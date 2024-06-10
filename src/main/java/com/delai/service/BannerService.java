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
	
	public Banner read(Long id) {
		return bannerRepository.findById(id).get();
	}
	
	public Banner update(Banner banner, Long id) {
		banner.setId(id);
		return bannerRepository.save(banner);
	}
	
	public void delete(Long id) {
		bannerRepository.deleteById(id);
	}
	
	public List<Banner> createMultiple(List<Banner> banners) {
		return bannerRepository.saveAll(banners);
	}
	
	public List<Banner> list() {
		return bannerRepository.findAll();
	}
	
	public void deleteMultiple(List<Long> bannerIds) {
		bannerRepository.deleteAllById(bannerIds);
	}
	
}
