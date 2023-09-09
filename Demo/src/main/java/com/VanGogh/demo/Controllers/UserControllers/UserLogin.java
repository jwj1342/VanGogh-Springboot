package com.VanGogh.demo.Controllers.UserControllers;

import com.VanGogh.demo.Controllers.Request.LoginRequest;
import com.VanGogh.demo.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UserLogin {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest, HttpSession session) {
        return userService.login(loginRequest, session);
    }

    @GetMapping("/protected")
    public String protectedEndpoint(HttpSession session) {
        return userService.protectedEndpoint(session);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        return userService.logout(session);
    }
}