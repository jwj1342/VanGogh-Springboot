package com.VanGogh.demo.Services;

import com.VanGogh.demo.Controllers.Request.LoginRequest;
import com.VanGogh.demo.Controllers.Request.RegisterRequest;
import com.VanGogh.demo.Controllers.Response.ErrorResponse;
import com.VanGogh.demo.Controllers.Response.LoginResponse;
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
     * @param request        注册请求对象
     * @param bindingResult  请求参数绑定结果
     * @return ResponseEntity 包含注册响应或错误响应
     */
    public ResponseEntity<?> registerUser(RegisterRequest request, BindingResult bindingResult) {
        // 检查请求参数是否有效
        if (bindingResult.hasErrors()) {
            // 返回错误信息
            ErrorResponse errorResponse = new ErrorResponse(422,"参数不合法",LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUserName())) {
            ErrorResponse errorResponse = new ErrorResponse(409,"用户名已存在",LocalDateTime.now());
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

    private static final long SESSION_TIMEOUT = 1800; // 会话超时时间（秒）

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录请求对象
     * @param session HttpSession对象
     * @return ResponseEntity 包含登录响应或错误响应
     */
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpSession session) {
        // 验证用户登录
        UserEntity user = userRepository.findUserEntityByUsername(loginRequest.getUserName());
        if (user == null || !user.getPassword().equals(hashPassword(loginRequest.getPassword()))) {
            ErrorResponse errorResponse = new ErrorResponse(401,"用户名或密码不正确", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 如果验证成功，创建JWT令牌并存储到会话中
        String jwtToken = generateJwtToken(user.getUsername());
        session.setAttribute("jwtToken", jwtToken);
        session.setMaxInactiveInterval((int) SESSION_TIMEOUT);

        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername(),user.getEmail(),"登录成功！",LocalDateTime.now());
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 受保护的端点
     *
     * @param session HttpSession对象
     * @return 受保护端点的响应信息
     */
    public String protectedEndpoint(HttpSession session) {
        // 检查会话中的JWT令牌是否有效
        String jwtToken = (String) session.getAttribute("jwtToken");
        if (jwtToken == null || !verifyJwtToken(jwtToken)) {
            return "未登录";
        }

        // 执行受保护的业务逻辑
        return "用户：" + getUsernameFromJwtToken(jwtToken);
    }

    /**
     * 用户登出
     *
     * @param session HttpSession对象
     * @return ResponseEntity 包含登出成功消息
     */
    public ResponseEntity<?> logout(HttpSession session) {
        // 销毁会话并清除会话中的JWT令牌
        session.invalidate();
        return ResponseEntity.ok("登出成功");
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
