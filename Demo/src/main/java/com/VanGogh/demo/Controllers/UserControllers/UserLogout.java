package com.VanGogh.demo.Controllers.UserControllers;

import com.VanGogh.demo.Repositories.UserRepository;
import com.VanGogh.demo.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UserLogout {
    @Autowired
    private UserService userService;
    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@Param("userName") String userName, HttpSession session) {
        return userService.logout(userName,session);
    }
}
