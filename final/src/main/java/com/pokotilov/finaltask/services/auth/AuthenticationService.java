package com.pokotilov.finaltask.services.auth;

import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;

public interface AuthenticationService {
    String register(RegisterUserRequest request);

    String authenticate(AuthUserRequest request);
}
