package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import com.pokotilov.finaltask.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Методы авторизации и регистрации.")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@Valid @RequestBody AuthUserRequest request) {
        return ResponseEntity.ok().body(authenticationService.authenticate(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody RegisterUserRequest request) {
        ResponseEntity<String> responseEntity = ResponseEntity.ok().body(authenticationService.register(request));
        kafkaTemplate.send("greetings", request.getEmail() + ";" + request.getLastName() + " " + request.getFirstName());
        return responseEntity;
    }
}