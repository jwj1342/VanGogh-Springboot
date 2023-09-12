package com.VanGogh.demo.Controllers.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录响应类，用于封装登录信息。
 */
@Data
public class LoginResponse {
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 状态码
     */
    private int statusCode;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    public LoginResponse(LocalDateTime timestamp, int statusCode, String username, String email) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.username = username;
        this.email = email;
    }
}
