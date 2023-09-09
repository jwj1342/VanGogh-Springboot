package com.VanGogh.demo.Controllers.ImageControllers;

import com.VanGogh.demo.Services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/image")
public class ImageUpload {
    @Autowired
    private ImageService imageService;
    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("userName") String userName,@RequestParam("title") String title, @RequestParam("imageFile") MultipartFile imageFile) {
        return imageService.uploadImage(userName, title,imageFile);
    }
}
