package com.VanGogh.demo.Controllers.Response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class LogoutResponse {
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

    public LogoutResponse(LocalDateTime timestamp, int statusCode, String username, String email) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.username = username;
        this.email = email;
    }
}
