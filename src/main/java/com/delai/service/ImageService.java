package com.delai.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.delai.model.Image;
import com.delai.repository.ImageRepository;

@Service
public class ImageService {

	@Autowired
	private ImageRepository imageRepository;
	
	public String upload(MultipartFile imageFile) throws IOException {
        var imageToSave = new Image(null, imageFile.getOriginalFilename(), 
        							imageFile.getContentType(),imageFile.getBytes()); 
        	
        imageRepository.save(imageToSave);
        
        return "File uploaded: " + imageFile.getOriginalFilename() + " (" + imageFile.getSize() + " bytes).";
    }

    public byte[] download(String imageName) {
        Optional<Image> dbImage = imageRepository.findByName(imageName);
        
        if (dbImage.isPresent())
        	return dbImage.get().getData();
        
        return new byte[0];
    }
	
    public void delete(String imageName) {
		imageRepository.deleteByName(imageName);
	}

	public List<String> list() {
		return imageRepository.findAll().stream().map(image -> image.getName()).toList();
	}

}
