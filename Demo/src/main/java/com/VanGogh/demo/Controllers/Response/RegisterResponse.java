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
     * 用户名
     */
    private String userName;
    /**
     * 注册成功信息
     */
    private String success;
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    public RegisterResponse(String userName, String success, LocalDateTime timestamp) {
        this.userName = userName;
        this.success = success;
        this.timestamp = timestamp;
    }
}
