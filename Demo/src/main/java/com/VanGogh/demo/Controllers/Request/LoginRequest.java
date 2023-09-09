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
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 持久化登录状态
     */
    private boolean persistLoginStatus;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    public LoginRequest(String password, String userName, boolean persistLoginStatus, LocalDateTime timestamp) {
        this.password = password;
        this.userName = userName;
        this.persistLoginStatus = persistLoginStatus;
        this.timestamp = timestamp;
    }
}
