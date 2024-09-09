package org.sergp.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sergp.userservice.dto.AuthRequest;
import org.sergp.userservice.dto.LoginRequest;
import org.sergp.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest authRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(authRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

    @GetMapping("/validateToken")
    public ResponseEntity<?> validateToken(HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.validateToken(request));
    }



}
