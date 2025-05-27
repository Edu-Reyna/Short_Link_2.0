package com.example.demo.controller;

import com.example.demo.controller.dto.AuthCreateUserRequest;
import com.example.demo.controller.dto.AuthLoginRequest;
import com.example.demo.controller.dto.AuthResponse;
import com.example.demo.services.implementation.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest authLoginRequest) {

        return new ResponseEntity<>(this.userDetailsService.loginUser(authLoginRequest), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthCreateUserRequest authCreateUserRequest) {
        return new ResponseEntity<>(this.userDetailsService.createUser(authCreateUserRequest), HttpStatus.CREATED);
    }

    @GetMapping("/oauth2/google")
    public ResponseEntity<AuthResponse> googleLogin(OAuth2AuthenticationToken authentication) {
        AuthResponse authResponse = this.userDetailsService.googleLogin(authentication);

        String redirectUrl = "http://localhost:4200/user?token=" + authResponse.jwt();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }

    @GetMapping("/oauth2/google/failure")
    public ResponseEntity<String> googleLoginRedirect(@RequestParam String code) {
        String redirectUri = "http://localhost:8080/oauth2/authorization/google";
        return new ResponseEntity<>(redirectUri, HttpStatus.OK);
    }
}
