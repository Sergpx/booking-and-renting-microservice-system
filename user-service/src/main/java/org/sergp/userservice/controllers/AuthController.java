package org.sergp.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sergp.userservice.dto.AuthRequest;
import org.sergp.userservice.dto.LoginRequest;
import org.sergp.userservice.services.AuthService;
import org.sergp.userservice.services.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @RequestMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(authRequest));
    }

    @RequestMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

    @GetMapping("/validateToken")
    public ResponseEntity<?> validateToken(HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.validateToken(request));
    }



}
