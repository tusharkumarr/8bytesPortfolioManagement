package com.portfolio.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.portfolio.management.model.GenericResponse;
import com.portfolio.management.model.LoginResponseData;
import com.portfolio.management.model.User;
import com.portfolio.management.service.UserService;
import com.portfolio.management.util.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public GenericResponse<User> signup(@RequestBody User user) {
        Optional<User> savedUser = userService.signup(user);
        if (savedUser.isEmpty()) {
            return GenericResponse.failure("User with this phone number already exists");
        }
        return GenericResponse.success("User registered successfully!", savedUser.get());
    }

    @PostMapping("/login")
    public GenericResponse<?> login(@RequestBody User request) {
        Optional<User> userOpt = userService.login(request.getPhoneNumber(), request.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = JwtUtil.generateToken(user.getPhoneNumber());

            return GenericResponse.success("Login successful",
                    new LoginResponseData(token, user.getId().toString(), user.getPhoneNumber()));
        }
        return GenericResponse.failure("Invalid credentials");
    }
}
