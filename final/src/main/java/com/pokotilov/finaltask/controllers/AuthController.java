package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.ExceptionResponse;
import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import com.pokotilov.finaltask.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Successful Operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "auth token"))),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class, hidden = true),
                            examples = @ExampleObject(
                                    name = "User with this email already exist",
                                    value = """
                                            {
                                                "type": "about:blank",
                                                "title": "Bad Request",
                                                "status": 400,
                                                "detail": "Failed to read request",
                                                "instance": "/api/auth/signup"
                                              }"""
                            ))),
            @ApiResponse(responseCode = "422",
                    description = "Unprocessable Entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class, hidden = true),
                            examples = {@ExampleObject(
                                    name = "Data validation error",
                                    value = """
                                              {
                                              "status": "UNPROCESSABLE_ENTITY",
                                              "message": "Data validation error",
                                              "map": {
                                                "password": "password should have 8 symbols at least, contain one upper and one lower case latin symbol and special character",
                                                "email": "wrong email"
                                              },
                                              "time": "2023-06-28 22:48:17"
                                            }"""
                            ), @ExampleObject(
                                    name = "Wrong email or password",
                                    value = """
                                              {
                                              "status": "BAD_REQUEST",
                                              "message": "Wrong email or password",
                                              "time": "2023-06-28 22:49:28"
                                            }"""
                            )}))
    })
    public ResponseEntity<String> logIn(@Valid @RequestBody AuthUserRequest request) {
        return ResponseEntity.ok().body(authenticationService.authenticate(request));
    }

    @PostMapping("/signup")
    @Operation(summary = "Регистрация пользователя", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Successful Operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "auth token"))),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class, hidden = true),
                            examples = {@ExampleObject(
                                    name = "User with this email already exist",
                                    value = """
                                            {
                                               "status": "BAD_REQUEST",
                                               "message": "User with this email already exist",
                                               "time": "2023-06-28 22:25:32"
                                             }"""
                            ), @ExampleObject(
                                    name = "Failed to read request",
                                    value = """
                                            {
                                                "type": "about:blank",
                                                "title": "Bad Request",
                                                "status": 400,
                                                "detail": "Failed to read request",
                                                "instance": "/api/auth/signup"
                                              }"""
                            )})),
            @ApiResponse(responseCode = "422",
                    description = "Unprocessable Entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class, hidden = true),
                            examples = @ExampleObject(
                                    name = "Data validation error",
                                    value = """
                                              {
                                              "status": "UNPROCESSABLE_ENTITY",
                                              "message": "Data validation error",
                                              "map": {
                                                "lastName": "last name is blank",
                                                "firstName": "first name is blank",
                                                "password": "password should contain one upper and one lower case latin symbol and special character",
                                                "phone": "phone is blank",
                                                "email": "wrong email"
                                              },
                                              "time": "2023-06-28 22:32:32"
                                            }"""
                            )))
    })
    public ResponseEntity<String> signUp(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok().body(authenticationService.register(request));
    }
}