package com.VanGogh.demo.Controllers.ImageControllers;

import com.VanGogh.demo.Controllers.Response.RecommendResponse;
import com.VanGogh.demo.Entities.ImageEntity;
import com.VanGogh.demo.Repositories.ImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/image")
public class ImageRecommend {
    private final ImageRepository imageRepository;

    public ImageRecommend(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/getRecommend")
    public ResponseEntity<List<RecommendResponse>> getRecommendPicture() {
        List<ImageEntity> images = imageRepository.findAll();
        List<ImageEntity> randomImages = getRandomImages(images, 6);
        List<RecommendResponse> customImages = new ArrayList<>();

        for (ImageEntity image : randomImages) {
            RecommendResponse customImage = new RecommendResponse();
            customImage.setUrl(image.getImageUrl());
            customImage.setTitle(image.getTitle());
            customImage.setLikes(image.getLikes());
            customImages.add(customImage);
        }
        return ResponseEntity.ok(customImages);
    }

    private List<ImageEntity> getRandomImages(List<ImageEntity> images, int count) {
        List<ImageEntity> randomImages = new ArrayList<>();
        Random random = new Random();

        int totalImages = images.size();
        if (totalImages <= count) {
            return images;
        }

        List<Integer> selectedIndexes = new ArrayList<>();
        while (selectedIndexes.size() < count) {
            int randomIndex = random.nextInt(totalImages);
            if (!selectedIndexes.contains(randomIndex)) {
                selectedIndexes.add(randomIndex);
                randomImages.add(images.get(randomIndex));
            }
            // 如果已经遍历了所有图片，但仍未选够指定数量的随机图片，则重新开始选择
            if (selectedIndexes.size() >= totalImages) {
                selectedIndexes.clear();
                randomImages.clear();
            }
        }

        return randomImages;
    }

}