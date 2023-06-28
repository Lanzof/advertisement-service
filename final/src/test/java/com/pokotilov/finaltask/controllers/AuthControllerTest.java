package com.pokotilov.finaltask.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokotilov.finaltask.advice.ControllerAdviceExceptionHandler;
import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import com.pokotilov.finaltask.services.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new ControllerAdviceExceptionHandler()).build();
    }


    @Test
    void logIn_validAuthRequest_returnToken() throws Exception {
        String token = "token";
        AuthUserRequest authUserRequest = AuthUserRequest.builder()
                .email("someEmail@gmail.com")
                .password("YourPassword23[]")
                .build();
        when(authenticationService.authenticate(authUserRequest)).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }

    @Test
    void logIn_invalidAuthRequest_returnToken() throws Exception {
        AuthUserRequest authUserRequest = AuthUserRequest.builder()
                .email("someEmail")
                .password("YourPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authUserRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.message").value("Data validation error"))
                .andExpect(jsonPath("$.map.size()").value("2"))
                .andExpect(jsonPath("$.map.password").value("password should have 8 symbols at least, contain one upper and one lower case latin symbol and special character"))
                .andExpect(jsonPath("$.map.email").value("wrong email"));
    }

    @Test
    void testSignUp() throws Exception {
        String token = "token";
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .email("someEmail@gmail.com")
                .password("YourPassword23[]")
                .firstName("Pablo")
                .lastName("Ganacci")
                .phone("+77894561212")
                .build();
        when(authenticationService.register(registerUserRequest)).thenReturn(token);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }
}