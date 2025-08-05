package com.kinari.kinari.controller;

import com.kinari.kinari.security.JwtUtil;
import com.kinari.kinari.dto.RegisterUserReqeust;
import com.kinari.kinari.entity.User;
import com.kinari.kinari.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserReqeust request) {
        UserDetailsServiceImpl userDetailsServiceImpl = (UserDetailsServiceImpl) userDetailsService;
        boolean result = userDetailsServiceImpl.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getFullName()
        );

        if (result) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Auth API is up!";
    }
}
