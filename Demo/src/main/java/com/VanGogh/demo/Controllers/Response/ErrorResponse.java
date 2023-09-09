package com.VanGogh.demo.Controllers.Response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错误响应类，用于封装错误信息。
 */
@Data
public class ErrorResponse {
    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 错误时间戳
     */
    private LocalDateTime timestamp;

    public ErrorResponse(int errorCode, String errorMessage, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }
}
