package com.VanGogh.demo.Services;

import com.VanGogh.demo.Controllers.Request.LoginRequest;
import com.VanGogh.demo.Controllers.Request.RegisterRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.LoginResponse;
import com.VanGogh.demo.Controllers.Response.LogoutResponse;
import com.VanGogh.demo.Controllers.Response.RegisterResponse;
import com.VanGogh.demo.Entities.UserEntity;
import com.VanGogh.demo.Repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    /**
     * 用户注册
     *
     * @param request       注册请求对象
     * @param bindingResult 请求参数绑定结果
     * @return ResponseEntity 包含注册响应或错误响应
     */
    public ResponseEntity<?> registerUser(RegisterRequest request, BindingResult bindingResult) {
        // 检查请求参数是否有效
        if (bindingResult.hasErrors()) {
            // 返回错误信息
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 422, "参数不合法", "/user/register");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUserName())) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 409, "用户名已存在", "/user/register");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // 对密码进行哈希加密处理
        String hashedPassword = hashPassword(request.getPassword());

        // 创建新用户并存储到数据库
        UserEntity user = new UserEntity();
        user.setUsername(request.getUserName());
        user.setPassword(hashedPassword);
        user.setRegistrationTime(LocalDateTime.now());
        userRepository.save(user);

        // 创建响应对象
        RegisterResponse registerResponse = new RegisterResponse(user.getUsername(), "成功", LocalDateTime.now());

        return ResponseEntity.ok(registerResponse);
    }

    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long SESSION_TIMEOUT = 10; // 会话超时时间（秒）

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录请求对象
     * @param session      HttpSession对象
     * @return ResponseEntity 包含登录响应或错误响应
     */

    public ResponseEntity<?> login(LoginRequest loginRequest, HttpSession session) {
        UserEntity user = userRepository.findUserEntityByUsername(loginRequest.getUserName());
        session.setAttribute("userName",loginRequest.getUserName());
        // 验证用户登录
        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "用户不存在", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        if (!user.getPassword().equals(hashPassword(loginRequest.getPassword()))) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "用户名或密码不正确", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        // 检查用户是否已经登录，如果已经登录则拒绝登录
        if (user.getIsLogin() == true) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "用户已登录", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 如果验证成功，创建JWT令牌并存储到会话中
        String jwtToken = generateJwtToken(user.getUsername());
        session.setAttribute("jwtToken", jwtToken);
        session.setMaxInactiveInterval((int) SESSION_TIMEOUT);


        user.setLogin(true);


        // 保存到数据库

        userRepository.save(user);

        LoginResponse loginResponse = new LoginResponse(user.getUsername(), user.getEmail(), "登录成功！", LocalDateTime.now());
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<?> protectedEndpoint(HttpSession session) {
        String userName= (String) session.getAttribute("userName");
        UserEntity user = userRepository.findUserEntityByUsername(userName);
        // 检查会话中的JWT令牌是否有效
        String jwtToken = (String) session.getAttribute("jwtToken");
        if (user.getIsLogin()==false||jwtToken == null || !verifyJwtToken(jwtToken)) {
            user.setLogin(false);
            userRepository.save(user);
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "未登录", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        // 执行受保护的业务逻辑
        LoginResponse loginResponse = new LoginResponse(userName, user.getEmail(), "登录成功！", LocalDateTime.now());
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<?> logout(String userName, HttpSession session) {
        try {
            // 根据用户名查询用户信息
            UserEntity user = userRepository.findUserEntityByUsername(userName);
            if (user.getIsLogin()) {

                // 清除登录状态
                user.setLogin(false);
                session.removeAttribute("jwtToken");

                // 更新用户信息
                userRepository.save(user);

                LogoutResponse logoutResponse = new LogoutResponse(userName, user.getEmail(), "登出成功！", LocalDateTime.now());
                return ResponseEntity.ok(logoutResponse);
            } else {
                // 处理未登录的情况
                ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "未登录", "/user/login");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            // 处理异常情况
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 500, "服务器错误", "/user/logout");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    private String generateJwtToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + SESSION_TIMEOUT * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    private boolean verifyJwtToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException ex) {
            return false;
        }
    }
}

