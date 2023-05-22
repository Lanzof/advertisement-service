package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.AuthenticationRequest;
import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.dto.RegisterRequest;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UserAlreadyExist;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public DefaultResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExist("User with this email already exist");
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
                .role(request.getRole())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new DefaultResponse(jwtToken);
    }

    public DefaultResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        var jwtToken = jwtService.generateToken(user);
        return new DefaultResponse(jwtToken);
    }
}
