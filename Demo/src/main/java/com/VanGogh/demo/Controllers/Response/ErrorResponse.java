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
    /**
     * 错误码
     */
    private int status;
    /**
     * 错误信息
     */
    private String error;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    /**
     * 错误路径
     */
    private String path;
}
