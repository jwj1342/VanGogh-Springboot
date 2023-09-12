package com.VanGogh.demo.Services;

import com.VanGogh.demo.Controllers.Request.ImageHandleRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.RecommendResponse;
import com.VanGogh.demo.Controllers.Response.UploadResponse;
import com.VanGogh.demo.Entities.ImageEntity;
import com.VanGogh.demo.Entities.UserEntity;
import com.VanGogh.demo.Repositories.ImageRepository;
import com.VanGogh.demo.Repositories.UserRepository;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 图片服务类，用于处理图片相关的业务逻辑。
 */
@Service
public class ImageService {
    @Value("${file.maxSize}")
    private DataSize maxFileSize;

    @Value("${obs.endpoint}")
    private String obsEndpoint;

    @Value("${obs.ak}")
    private String obsAk;

    @Value("${obs.sk}")
    private String obsSk;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;

    /**
     * 上传图片并保存到数据库。
     *
     * @param userName  用户名
     * @param title     图片标题
     * @param imageFile 要上传的图片文件
     * @return ResponseEntity对象，包含上传结果或错误信息
     */
    public ResponseEntity<?> uploadImage(String userName, String title, MultipartFile imageFile) {
        try {
            if (userName==null) {
                ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "上传失败，用户未找到！ ", "/image.upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (userRepository.findUserEntityByUsername(userName)==null) {
                ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "上传失败，用户未找到！ ", "/image.upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (imageFile.isEmpty()) {
                ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "上传失败，图片未找到！ ", "/image.upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // 验证文件大小
            if (imageFile.getSize() > maxFileSize.toBytes()) {
                ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 413, "上传失败，最大上传10M！", "/image.upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            // 图片实体创建
            ImageEntity imageEntity = new ImageEntity();
            String imgUrlBefore=saveImage(imageFile);
            imageEntity.setImageUrl(imgUrlBefore);

            //图片处理请求封装
//            ImageHandleRequest imageHandleRequest=new ImageHandleRequest();
//            imageHandleRequest.setImageUrlBefore(imgUrlBefore);
//            imageHandleRequest.setTimestamp(LocalDateTime.now());
            //发送图片处理请求
            //String imgUrlAfter=
            //处理后的url赋值
            //imageEntity.setImageUrlAfter();
            //用户仓库创建，根据用户名找到用户实体
            UserEntity user = userRepository.findUserEntityByUsername(userName);
            imageEntity.setUser(user);
            imageEntity.setTitle(title);
            imageEntity.setCreateTime(LocalDateTime.now());
            imageRepository.save(imageEntity);

            //创建数组添加
            List<UploadResponse> uploadResponseList = new ArrayList<>();
            List<ImageEntity> imageEntities = imageRepository.findAllByUserId(user.getId());

            for (ImageEntity imageEntity1 : imageEntities) {
                UploadResponse uploadResponse = new UploadResponse();
                uploadResponse.setTimestamp(LocalDateTime.now());
                uploadResponse.setImageUrl(imageEntity1.getImageUrl());
                uploadResponseList.add(uploadResponse);
            }

            // 根据时间戳从新到旧排序
            Collections.sort(uploadResponseList, (r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp())); // 降序排序

            return ResponseEntity.ok(uploadResponseList);
        } catch (IOException e) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 400, "上传失败: " + e.getMessage(), "/image/upload");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 将图片文件保存到OBS，并返回图片URL。
     *
     * @param imageFile 要保存的图片文件
     * @return 保存后的图片URL
     * @throws IOException 如果保存失败则抛出IOException
     */
    private String saveImage(MultipartFile imageFile) throws IOException {
        // 生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imageFile.getOriginalFilename());
        ObsClient obsClient = new ObsClient(obsAk, obsSk, obsEndpoint);
        // 保存文件到OBS
        PutObjectRequest request = new PutObjectRequest("vangogh-test", fileName, imageFile.getInputStream());
        PutObjectResult result = obsClient.putObject(request);
//        if (result.getStatusCode() != 200) {
//            throw new IOException("存储失败: " + result.getStatusCode());
//        }

        // 生成文件的URL
        return "http://vangogh-test.obs.cn-north-4.myhuaweicloud.com/images/" + fileName;
    }

    /**
     * 获取推荐的图片列表。
     *
     * @return ResponseEntity对象，包含推荐图片列表或错误信息
     */
    public ResponseEntity<List<RecommendResponse>> getRecommendPicture() {
        List<ImageEntity> images = imageRepository.findAll();
        List<ImageEntity> randomImages = getRandomImages(images, 6);
        List<RecommendResponse> customImages = new ArrayList<>();

        for (ImageEntity image : randomImages) {
            RecommendResponse customImage = new RecommendResponse();
            customImage.setUrl(image.getImageUrl());
            customImage.setTitle(image.getTitle());
            customImage.setLikes(image.getLikes());
            customImage.setTimestamp(LocalDateTime.now());
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
