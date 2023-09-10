package com.VanGogh.demo.Controllers.ImageControllers;

import com.VanGogh.demo.Controllers.Response.RecommendResponse;
import com.VanGogh.demo.Services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/image")
public class ImageRecommend {
    @Autowired
    private ImageService imageService;

    @GetMapping(path = "/getRecommend")
    public ResponseEntity<List<RecommendResponse>> getRecommendPicture() {
        return imageService.getRecommendPicture();
    }
}
