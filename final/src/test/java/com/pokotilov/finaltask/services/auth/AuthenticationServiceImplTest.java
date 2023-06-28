package com.pokotilov.finaltask.services.auth;

import com.pokotilov.finaltask.dto.user.AuthUserRequest;
import com.pokotilov.finaltask.dto.user.RegisterUserRequest;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.ConflictException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.security.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtServiceImpl jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void register_userNoReferral_returnToken() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("test@test.com")
                .password("password")
                .phone("1234567890")
                .firstName("Test")
                .lastName("User")
                .description("Test user description")
                .build();

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .description(request.getDescription())
                .rating(0f)
                .ban(false)
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class)))
                .thenReturn("token");
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);

        // Act
        String result = authenticationService.register(request);

        // Assert
        verify(userRepository).save(savedUser.capture());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(jwtService).generateToken(any(User.class));

        assertEquals(user.getEmail(), savedUser.getValue().getEmail());
        assertEquals("token", result);
    }

    @Test
    void register_adminWithReferral_returnToken() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("test@test.com")
                .password("password")
                .phone("1234567890")
                .firstName("Test")
                .lastName("User")
                .description("Test user description")
                .referralCode("adminCode")
                .build();

        ReflectionTestUtils.setField(authenticationService, "adminCode", "adminCode");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .description(request.getDescription())
                .rating(0f)
                .ban(false)
                .role(Role.ADMIN)
                .build();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class)))
                .thenReturn("token");
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);

        // Act
        String result = authenticationService.register(request);

        // Assert
        verify(userRepository).save(savedUser.capture());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(jwtService).generateToken(any(User.class));

        assertEquals(user.getEmail(), savedUser.getValue().getEmail());
        assertEquals("token", result);
    }

    @Test
    void register_userWithWrongReferral_shouldThrow() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("test@test.com")
                .password("password")
                .phone("1234567890")
                .firstName("Test")
                .lastName("User")
                .description("Test user description")
                .referralCode("wrongCode")
                .build();

        ReflectionTestUtils.setField(authenticationService, "adminCode", "adminCode");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        // Act
        assertThrows(ConflictException.class, () -> authenticationService.register(request), "Wrong referral code");
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void register_userNotUniqEmail_shouldThrow() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("test@test.com")
                .build();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);
        // Act
        assertThrows(BadRequestException.class, () -> authenticationService.register(request), "User with this email already exist");
        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void authenticate_validUser_returnToken() {
        // Arrange
        AuthUserRequest request = AuthUserRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();
        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        // Act
        String result = authenticationService.authenticate(request);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertEquals("token", result);
    }

    @Test
    void authenticate_wrongCredentials_shouldThrow() {
        // Arrange
        AuthUserRequest request = AuthUserRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Authentication failed") {});

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authenticationService.authenticate(request), "Wrong email or password");

        verifyNoMoreInteractions(userRepository, jwtService);
    }

    @Test
    void authenticate_userNotExist_shouldThrow() {
        // Setup
        AuthUserRequest request = AuthUserRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // Execution & Verification
        assertThrows(NotFoundException.class, () -> authenticationService.authenticate(request), "User doesn't exist");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoMoreInteractions(jwtService);
    }
}