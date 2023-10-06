package com.VanGogh.demo.Controllers.Request;

import com.VanGogh.demo.Entities.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public class UploadRequest {
    private String userName;
    private MultipartFile imageFile;

    public String getUserName() {
        return userName;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }
}
