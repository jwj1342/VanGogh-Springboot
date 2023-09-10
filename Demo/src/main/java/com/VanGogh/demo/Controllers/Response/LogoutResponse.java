package com.VanGogh.demo.Controllers.Response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class LogoutResponse {
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 登出成功标志
     */
    private String success;

    public LogoutResponse(String username, String email,  String success,LocalDateTime timestamp) {
        this.username = username;
        this.email = email;
        this.timestamp = timestamp;
        this.success = success;
    }
}
