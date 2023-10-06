package com.VanGogh.demo.Controllers.Response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错误响应类，用于封装错误信息。
 */
@Data
public class ErrorResponse {
    /**
     * 错误时间戳
     */
    private LocalDateTime timestamp;

    public ErrorResponse(LocalDateTime timestamp, int statusCode, String error, String path) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.error = error;
        this.path = path;
    }

    /**
     * 错误码
     */
    private int statusCode;
    /**
     * 错误信息
     */
    private String error;

    /**
     * 错误路径
     */
    private String path;
}
