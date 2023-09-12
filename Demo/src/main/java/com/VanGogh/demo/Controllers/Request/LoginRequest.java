package com.VanGogh.demo.Controllers.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录请求类，用于接收登录请求的参数。
 */
@Data
public class LoginRequest {

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    public LoginRequest(String password, String userName,  LocalDateTime timestamp) {
        this.password = password;
        this.userName = userName;
        this.timestamp = timestamp;
    }
}
