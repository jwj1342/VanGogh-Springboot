package com.VanGogh.demo.Controllers.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 推荐响应类，用于封装推荐信息。
 */
@Data
public class RecommendResponse {
    /**
     * 标题
     */
    private String title;
    /**
     * 图片url
     */
    private String url;
    /**
     * 点赞数
     */
    private int likes;
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    /**
     * 获取推荐成功标志
     */
    private String success;
}
