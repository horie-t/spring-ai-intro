package com.example.spring_ai_demo.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // 認証済みのユーザーにアクセスされるとユーザー情報を返す（または200 OK）
    // 未認証の場合はSpring Securityが401を返す
    @GetMapping("/me")
    public Principal getAuthenticatedUser(Principal principal) {
        return principal;
    }
}