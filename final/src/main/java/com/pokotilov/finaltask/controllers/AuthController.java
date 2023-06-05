package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import com.pokotilov.finaltask.services.auth.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Методы авторизации и регистрации.")
public class AuthController {

    private final IAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@Valid @RequestBody AuthUserRequest request) {
        return ResponseEntity.ok().body(authenticationService.authenticate(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok().body(authenticationService.register(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        authenticationService.logout(request, response);
        return ResponseEntity.ok().body("Logout success");
    }
}