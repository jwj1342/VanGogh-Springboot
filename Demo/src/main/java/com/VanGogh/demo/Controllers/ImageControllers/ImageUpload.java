package com.VanGogh.demo.Controllers.ImageControllers;

import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.UploadResponse;
import com.VanGogh.demo.Entities.ImageEntity;
import com.VanGogh.demo.Repositories.ImageRepository;
import com.VanGogh.demo.Repositories.UserRepository;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(path = "/image")
public class ImageUpload {

    private static final String UPLOAD_DIRECTORY = "upload_directory";
    private static final DataSize MAX_FILE_SIZE = DataSize.ofMegabytes(10); // 最大文件大小为 10MB
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("userName") String userName
            , @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                ErrorResponse errorResponse = new ErrorResponse("No image file provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // 验证文件大小
            if (imageFile.getSize() > MAX_FILE_SIZE.toBytes()) {
                ErrorResponse errorResponse = new ErrorResponse("File size exceeds maximum allowed limit of 10MB");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

//            // 验证文件类型
//            if (!isValidImageType(imageFile.getContentType())) {
//                ErrorResponse errorResponse = new ErrorResponse("Invalid image file type");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//            }
            ImageEntity imageEntity=new ImageEntity();
            imageEntity.setImageUrl(saveImage(imageFile));
            imageEntity.setUser(userRepository.findUserEntityByUsername(userName));
            imageRepository.save(imageEntity);

            UploadResponse uploadResponse = new UploadResponse();
            uploadResponse.setImageUrl(imageEntity.getImageUrl());

            return ResponseEntity.ok(uploadResponse);
        } catch (IOException e) {
            ErrorResponse errorResponse = new ErrorResponse("Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private boolean isValidImageType(String contentType) {
        // 简单示例：仅允许 image/jpeg 和 image/png 类型的文件
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        // 生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imageFile.getOriginalFilename());

        // OBS对象存储相关配置信息
        String endPoint = "obs.cn-north-4.myhuaweicloud.com"; // OBS服务节点
        String ak = "OSZR0X5FEP0MLU3GGROH"; // Access Key
        String sk = "SHTp0pllFaj9sfnJcCi2p1X1ir0nBTbREkuUKNan"; // Secret Key
        String bucketName = "vangogh-test"; // 桶名称

        try {
            // 创建OBS客户端
            ObsClient obsClient = new ObsClient(ak, sk, endPoint);

            // 保存文件到OBS
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, imageFile.getInputStream());
            PutObjectResult result = obsClient.putObject(request);
            if (result.getStatusCode() != 200) {
                throw new IOException("Failed to save image to OBS. Status code: " + result.getStatusCode());
            }

            // 生成文件的URL
            String imageUrl = "http://" + bucketName + "." + endPoint + "/images/" + fileName;
            obsClient.close();
            return imageUrl;
        } catch (ObsException e) {
            throw new IOException("Failed to save image to OBS: " + e.getMessage(), e);
        }
    }
}
