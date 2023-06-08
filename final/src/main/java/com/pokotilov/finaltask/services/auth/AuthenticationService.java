package com.pokotilov.finaltask.services.auth;

import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    String register(RegisterUserRequest request);

    String authenticate(AuthUserRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
