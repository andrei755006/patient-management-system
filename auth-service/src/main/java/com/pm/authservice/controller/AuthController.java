package com.pm.authservice.controller;

import com.pm.authservice.dto.AuthRequest;
import com.pm.authservice.dto.AuthResponse;
import com.pm.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) throws Exception {
        String token = authService.register(request.email(), request.password());
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) throws Exception {
        String token = authService.login(request.email(), request.password());
        return new AuthResponse(token);
    }
}