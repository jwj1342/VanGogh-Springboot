package com.VanGogh.demo.Controllers.ImageControllers;

import com.VanGogh.demo.Controllers.Request.UploadRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.UploadResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(path="/image")
public class ImageUpload {

    private static final String UPLOAD_DIRECTORY = "upload_directory";
    private static final DataSize MAX_FILE_SIZE = DataSize.ofMegabytes(10); // 最大文件大小为 10MB

    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadImage(@RequestBody UploadRequest uploadRequest) {
        try {
            MultipartFile imageFile = uploadRequest.getImageFile();

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

            // 保存文件到服务器
            String imageUrl = saveImage(imageFile);

            UploadResponse uploadResponse = new UploadResponse();
            uploadResponse.setImageUrl(imageUrl);

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
        String endPoint = "your_obs_endpoint"; // 替换为您的OBS服务节点
        String ak = "your_access_key"; // 替换为您的Access Key
        String sk = "your_secret_key"; // 替换为您的Secret Key
        String bucketName = "your_bucket_name"; // 替换为您的桶名称

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

            return imageUrl;
        } catch (ObsException e) {
            throw new IOException("Failed to save image to OBS: " + e.getMessage(), e);
        }
    }
}
