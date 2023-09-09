package com.VanGogh.demo.Controllers.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 上传响应类，用于封装上传图片的信息。
 */
@Data
public class UploadResponse {
    /**
     * 上传图片的url
     */
    private String imageUrl;
    /**
     * 上传成功信息
     */
    private String success;
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}
