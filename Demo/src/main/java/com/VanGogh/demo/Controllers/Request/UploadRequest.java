package com.VanGogh.demo.Controllers.Request;

import org.springframework.web.multipart.MultipartFile;

/**
 * 上传请求类，用于接收上传请求的参数。
 */
public class UploadRequest {
    private String userName;
    private MultipartFile imageFile;

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 获取图像文件。
     *
     * @return 图像文件
     */
    public MultipartFile getImageFile() {
        return imageFile;
    }
}
