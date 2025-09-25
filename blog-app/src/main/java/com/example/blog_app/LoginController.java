package com.example.blog_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

/**
 * Handles authentication endpoints.
 * Manages admin login and JWT token generation.
 */
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final JwtUtil jwtUtil;

    @Value("${app.admin.user}")
    private String adminUser;

    @Value("${app.admin.pass}")
    private String adminPass;

    public LoginController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login endpoint for admin user
     * Accepts JSON body with "username" and "password"
     * Returns JWT token if credentials are correct, otherwise returns error 401
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        System.out.println("LoginController reached with request");
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (adminUser.equals(username) && adminPass.equals(password)) {
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials!");
    }
}
