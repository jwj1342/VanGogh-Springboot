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
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
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
     * 登录成功标志
     */
    private String success;

    public LoginResponse(Long id, String username, String email,  String success,LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.timestamp = timestamp;
        this.success = success;
    }
}
