package com.VanGogh.demo.Controllers.UserControllers;

import com.VanGogh.demo.Controllers.Request.LoginRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.LoginResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
@RestController
@RequestMapping(path="/user")
public class UserLogin {
    private static final String SECRET_KEY = "mySecretKey"; // JWT密钥
    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long SESSION_TIMEOUT = 1800; // 会话超时时间（秒）

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest, HttpSession session) {
        // 验证用户登录
        UserEntity user = userRepository.findUserEntityByUsername(loginRequest.getUserName());
        if (user == null || !user.getPassword().equals(hashPassword(loginRequest.getPassword()))) {
            ErrorResponse errorResponse = new ErrorResponse("用户名或密码不正确");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 如果验证成功，创建JWT令牌并存储到会话中
        String jwtToken = generateJwtToken(user.getUsername());
        session.setAttribute("jwtToken", jwtToken);
        session.setMaxInactiveInterval((int) SESSION_TIMEOUT);

        LoginResponse loginResponse = new LoginResponse(user.getId(),user.getUsername(),user.getEmail());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/protected")
    public String protectedEndpoint(HttpSession session) {
        // 检查会话中的JWT令牌是否有效
        String jwtToken = (String) session.getAttribute("jwtToken");
        if (jwtToken == null || !verifyJwtToken(jwtToken)) {
            return "未登录";
        }

        // 执行受保护的业务逻辑
        return "用户：" + getUsernameFromJwtToken(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 销毁会话并清除会话中的JWT令牌
        session.invalidate();
        return ResponseEntity.ok("登出成功");
    }

    // 哈希加密密码
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    // 生成JWT令牌
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

    // 验证JWT令牌
    private boolean verifyJwtToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException ex) {
            // JWT令牌无效，可能是签名错误、过期或格式错误
            return false;
        }
    }

    // 从JWT令牌中获取用户名
    private String getUsernameFromJwtToken(String jwtToken) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody().getSubject();
    }
}