package com.delai.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.delai.service.ImageService;

@RestController
@RequestMapping("/image")
public class ImageController {

	@Autowired
	private ImageService imageService;
	
	@PostMapping()
	@ResponseStatus(code = HttpStatus.OK)
	public String create(@RequestParam("image") MultipartFile file) throws IOException {
		return imageService.upload(file);
	}
	
	@GetMapping("/{imageName}")
	public ResponseEntity<byte[]> read(@PathVariable String imageName) {
		return ResponseEntity.ok().contentType(MediaType.valueOf("image/webp")).body(imageService.download(imageName));
	}
	
	@DeleteMapping("/{imageName}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String imageName) {
		imageService.delete(imageName);
	}
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<String> list() {
		return imageService.list();
	}
	
}
