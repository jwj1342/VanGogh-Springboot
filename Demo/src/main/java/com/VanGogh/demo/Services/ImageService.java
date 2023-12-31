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
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 图片服务类，用于处理图片相关的业务逻辑。
 */
@Service
public class ImageService {
    //    @Value("${file.maxSize}")
//    private DataSize maxFileSize;
// 默认单个文件大小为1MB
    private DataSize maxFileSize = DataSize.ofMegabytes(50L);

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
    ImageHandleRequest imageHandleRequest = new ImageHandleRequest();

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
            if (userName == null) {
                ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "上传失败，用户未找到！ ", "/image.upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (userRepository.findUserEntityByUsername(userName) == null) {
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
            String imgUrlBefore = saveImage(imageFile);
            imageEntity.setImageUrl(imgUrlBefore);

            //图片处理请求封装
            imageHandleRequest.setImage_url(imgUrlBefore);

            //发送图片处理请求
            String imgUrlAfter = saveHandledImage(getHandledImage(imageHandleRequest));
            //处理后的url赋值
            imageEntity.setImageUrlAfter(imgUrlAfter);
            //用户仓库创建，根据用户名找到用户实体
            UserEntity user = userRepository.findUserEntityByUsername(userName);
            imageEntity.setUser(user);
            imageEntity.setTitle(title);
            imageEntity.setCreateTime(LocalDateTime.now());
            imageRepository.save(imageEntity);

            UploadResponse uploadResponse = new UploadResponse();
            uploadResponse.setImageUrl(imgUrlBefore);
            uploadResponse.setImageUrlAfter(imgUrlAfter);
            uploadResponse.setStatusCode(200);
            uploadResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(uploadResponse);
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
        if (result.getStatusCode() != 200) {
            throw new IOException("存储失败: " + result.getStatusCode());
        }

        // 生成文件的URL
        return "http://vangogh-test.obs.cn-north-4.myhuaweicloud.com/" + fileName;
    }

    private String saveHandledImage(MultipartFile imageFile) throws IOException {
        // 生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imageFile.getOriginalFilename());
        ObsClient obsClient = new ObsClient(obsAk, obsSk, obsEndpoint);
        // 保存文件到OBS
        PutObjectRequest request = new PutObjectRequest("vangogh-test", fileName + "png", imageFile.getInputStream());
        PutObjectResult result = obsClient.putObject(request);
        if (result.getStatusCode() != 200) {
            throw new IOException("存储失败: " + result.getStatusCode());
        }
        // 生成文件的URL
        return "http://vangogh-test.obs.cn-north-4.myhuaweicloud.com/" + fileName + "png";
    }

    /**
     * 获取处理后的图片
     *
     * @return 字节流
     */
    public MultipartFile getHandledImage(ImageHandleRequest imageHandleRequest) throws IOException {
        String url = "http://139.9.235.250:5000/process_pic";

        // 创建请求体
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ImageHandleRequest> requestEntity = new HttpEntity<>(imageHandleRequest, headers);

        // 发送POST请求并获取响应
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);
        byte[] imageBytes = responseEntity.getBody();

        // 将字节数组转换为MultipartFile对象
        return new MockMultipartFile("handled_image.png", imageBytes);
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
            customImage.setUrl(image.getImageUrlAfter());
            customImage.setTitle(image.getTitle());
            customImage.setLikes(image.getLikes());
            customImage.setStatusCode(200);
            customImage.setTimestamp(image.getCreateTime());
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
