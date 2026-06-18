package com.example.chatroom.controller;

import com.example.chatroom.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     HttpServletRequest request) {
        return authService.login(username, password, request);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> loginJson(@RequestBody Map<String, String> body,
                                         HttpServletRequest request) {
        return authService.login(body.get("username"), body.get("password"), request);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        @RequestParam(name = "nickname", required = false, defaultValue = "") String nickname) {
        return authService.register(username, password, nickname);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> registerJson(@RequestBody Map<String, String> body) {
        return authService.register(body.get("username"), body.get("password"), body.get("nickname"));
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleError(Exception e) {
        return Collections.singletonMap("message", "服务器内部错误: " + e.getMessage());
    }
}
