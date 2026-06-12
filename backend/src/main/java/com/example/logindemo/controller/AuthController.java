package com.example.logindemo.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final String validUsername = "admin";
    private final String validPassword = "admin123";

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("=== 登录请求 ===");
        System.out.println("客户端IP: " + request.getRemoteAddr());
        System.out.println("请求方法: " + request.getMethod());
        System.out.println("请求URL: " + request.getRequestURL());
        System.out.println("用户名: " + username);
        System.out.println("密码: " + password);
        
        if (validUsername.equals(username) && validPassword.equals(password)) {
            System.out.println("登录成功");
            response.put("success", true);
            response.put("message", "登录成功");
        } else {
            System.out.println("登录失败 - 用户名或密码错误");
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        return response;
    }

    @GetMapping("/test")
    public String test() {
        return "访问成功";
    }
}
