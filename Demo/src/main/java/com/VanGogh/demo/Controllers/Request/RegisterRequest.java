package com.VanGogh.demo.Controllers.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 注册请求类，用于接收注册请求的参数。
 */
@Data
public class RegisterRequest {

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
}
