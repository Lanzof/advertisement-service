package com.pokotilov.finaltask.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.security.JwtService;
import com.pokotilov.finaltask.services.auth.IAuthenticationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@SpringBootTest
//@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IAuthenticationService authService;
    @MockBean
    private JwtService jwtService;


    @Test
    @SneakyThrows
    void validLogIn_ReturnsToken() {
        // Arrange
        AuthUserRequest validRequest = AuthUserRequest.builder()
                .email("someEmail@gmail.com")
                .password("YourPassword23[]")
                .build();
        String token = "token";
        given(authService.authenticate(validRequest)).willReturn(token);

        // Act
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        assertEquals(responseContent, token);
    }

    @Test
    @Disabled
    void signUp() {

    }

    @Test
    @Disabled
    void logout() {
    }

    @SneakyThrows
    private static String asJsonString(final Object obj) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(obj);
    }
}