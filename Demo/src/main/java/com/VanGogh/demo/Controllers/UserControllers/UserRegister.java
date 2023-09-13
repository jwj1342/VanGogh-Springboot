/**
 * 用户注册控制器
 */
package com.VanGogh.demo.Controllers.UserControllers;

import com.VanGogh.demo.Controllers.Request.RegisterRequest;
import com.VanGogh.demo.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/user")
public class UserRegister {
    @Autowired
    private UserService userService;
    /**
     * 用户注册
     *
     * @param request        注册请求对象
     * @return ResponseEntity 包含注册响应或错误响应
     */
    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request, HttpSession session) {
        return userService.registerUser(request,session);
    }

}
