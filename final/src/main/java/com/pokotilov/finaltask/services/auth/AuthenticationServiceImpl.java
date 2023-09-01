package com.pokotilov.finaltask.services.auth;

import com.pokotilov.finaltask.aop.LogExecution;
import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.ConflictException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.security.JwtService;
import com.pokotilov.finaltask.services.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${auth.adminCode}")
    private String adminCode;

    @LogExecution
    public String register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User with this email already exist");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .description(request.getDescription())
                .rating((float) 0)
                .ban(false)
                .build();

        user.setRole(request.getReferralCode() != null
                ? checkAdminCode(request.getReferralCode()) : Role.USER);

        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    private Role checkAdminCode(String refCode) {
        if (adminCode.equals(refCode)) {
            return Role.ADMIN;
        } else {
            throw new ConflictException("Wrong referral code");
        }
    }

    @LogExecution
    public String authenticate(AuthUserRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadRequestException("Wrong email or password");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User doesn't exist"));
        return jwtService.generateToken(user);
    }
}
