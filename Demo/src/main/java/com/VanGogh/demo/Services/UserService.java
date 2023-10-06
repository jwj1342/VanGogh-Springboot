package com.VanGogh.demo.Services;

import com.VanGogh.demo.Controllers.Request.LoginRequest;
import com.VanGogh.demo.Controllers.Request.LogoutRequest;
import com.VanGogh.demo.Controllers.Request.ProtectedRequest;
import com.VanGogh.demo.Controllers.Request.RegisterRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.LoginResponse;
import com.VanGogh.demo.Controllers.Response.LogoutResponse;
import com.VanGogh.demo.Controllers.Response.RegisterResponse;
import com.VanGogh.demo.Entities.UserEntity;
import com.VanGogh.demo.Repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${session.timeout}")
    private int sessionTimeout;

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
     * @param request 注册请求对象
     * @return ResponseEntity 包含注册响应或错误响应
     */
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (request.getUserName() == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "注册失败，用户名为空！ ", "/user/register");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUserName())) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 409, "用户名已存在！", "/user/register");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // 对密码进行哈希加密处理
        String hashedPassword = hashPassword(request.getPassword());

        // 创建新用户并存储到数据库
        UserEntity user = new UserEntity();
        user.setUsername(request.getUserName());
        user.setPassword(hashedPassword);
        user.setRegistrationTime(LocalDateTime.now());
        user.setLoginStatus(true);
        userRepository.save(user);
        // 创建响应对象
        RegisterResponse registerResponse = new RegisterResponse(LocalDateTime.now(), 200, user.getUsername());

        return ResponseEntity.ok(registerResponse);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录请求对象
     * @return ResponseEntity 包含登录响应或错误响应
     */

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        if (loginRequest.getUserName() == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 400, "用户名为空", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        UserEntity user = userRepository.findUserEntityByUsername(loginRequest.getUserName());
        // 验证用户登录
        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "用户不存在", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        if (!user.getPassword().equals(hashPassword(loginRequest.getPassword()))) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 400, "用户密码错误", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        if (user.getLoginStatus() == true) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 409, "用户已登录", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        // 如果验证成功
        user.setLoginStatus(true);
        userRepository.save(user);
        LoginResponse loginResponse = new LoginResponse(LocalDateTime.now(), 200, user.getUsername(), user.getEmail());
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<?> protectedEndpoint(ProtectedRequest protectedRequest) {
        if (protectedRequest.getUserName() == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "用户不存在", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        UserEntity user = userRepository.findUserEntityByUsername(protectedRequest.getUserName());
        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "用户不存在", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        if (user.getLoginStatus() == false) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "用户未登录", "/user/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        LoginResponse loginResponse = new LoginResponse(LocalDateTime.now(), 200, user.getUsername(), user.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
    }


    public ResponseEntity<?> logout(LogoutRequest logoutRequest) {
        if (logoutRequest.getUserName() == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "用户不存在", "/user/logout");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        UserEntity user = userRepository.findUserEntityByUsername(logoutRequest.getUserName());
        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "用户不存在", "/user/logout");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        if (user.getLoginStatus() == false) {
            ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "用户未登录", "/user/logout");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        //修改登录状态
        user.setLoginStatus(false);
        userRepository.save(user);
        LogoutResponse logoutResponse = new LogoutResponse(LocalDateTime.now(), 200, user.getUsername(), user.getEmail());
        return ResponseEntity.ok(logoutResponse);
    }


//    private String generateJwtToken(String username) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + sessionTimeout * 1000);
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, key)
//                .compact();
//    }

//    private boolean verifyJwtToken(String jwtToken) {
//        try {
//            Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
//            return true;
//        } catch (ExpiredJwtException | MalformedJwtException ex) {
//            return false;
//        }
//    }
//
//    private String getUsernameFromJwtToken(String jwtToken) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(jwtToken)
//                .getBody();
//
//        return claims.getSubject();
//    }

}

