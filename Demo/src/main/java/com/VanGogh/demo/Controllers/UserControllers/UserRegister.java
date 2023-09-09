/**
 * 用户注册控制器
 */
package com.VanGogh.demo.Controllers.UserControllers;

import com.VanGogh.demo.Controllers.Request.RegisterRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.RegisterResponse;
import com.VanGogh.demo.Entities.UserEntity;
import com.VanGogh.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
@RequestMapping(path="/user")
public class UserRegister {
    /**
     * 密码加密方法 - SHA-256哈希加密
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册
     *
     * @param request        注册请求对象
     * @param bindingResult  请求参数绑定结果
     * @return ResponseEntity 包含注册响应或错误响应
     */
    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request, BindingResult bindingResult) {
        // 检查请求参数是否有效
        if (bindingResult.hasErrors()) {
            // 返回错误信息
            ErrorResponse errorResponse = new ErrorResponse("参数不合法");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUserName())) {
            ErrorResponse errorResponse = new ErrorResponse("用户名已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // 对密码进行哈希加密处理
        String hashedPassword = hashPassword(request.getPassword());

        // 创建新用户并存储到数据库
        UserEntity user = new UserEntity();
        user.setUsername(request.getUserName());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // 创建响应对象
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserName(user.getUsername());
        registerResponse.setPassword(user.getPassword());

        return ResponseEntity.ok(registerResponse);
    }
}
