package com.VanGogh.demo.Controllers.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 注册响应类，用于封装注册信息。
 */
@Data
public class RegisterResponse {
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    /**
     * 状态码
     */
    private int statusCode;

    public RegisterResponse(LocalDateTime timestamp, int statusCode, String userName) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.userName = userName;
    }

    /**
     * 用户名
     */
    private String userName;
}
