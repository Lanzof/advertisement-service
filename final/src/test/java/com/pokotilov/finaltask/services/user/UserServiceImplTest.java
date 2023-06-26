package com.pokotilov.finaltask.services.user;

import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.UserMapper;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_validPageable_returnPage() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(User.builder().id(1L).firstName("Test User 1").rating(5f).build());
        users.add(User.builder().id(1L).firstName("Test User 2").rating(2f).build());
        users.add(User.builder().id(1L).firstName("Test User 3").rating(3f).build());

        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(page);
        when(userMapper.toDto(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return UserDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .rating(user.getRating())
                            .build();
                });
        Pageable pageable = Pageable.ofSize(10);

        // Act
        Page<UserDto> output = userService.getAllUsers(pageable);

        // Assert
        List<UserDto> dtos = output.getContent();
        assertEquals(users.get(0).getId(), dtos.get(0).getId());
        assertEquals(users.get(1).getId(), dtos.get(1).getId());
        assertEquals(users.get(2).getId(), dtos.get(2).getId());

    }

    @Test
    void getUser_validId_returnUserDto() {
        // Arrange
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class)))
                .thenAnswer(invocation -> {
                    User userForMap = invocation.getArgument(0);
                    return UserDto.builder()
                            .id(userForMap.getId())
                            .build();
                });

        // Act
        UserDto output = userService.getUser(userId);

        // Assert
        assertEquals(user.getId(), output.getId());
    }

    @Test
    void getUser_notExistingId_shouldThrow() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getUser(userId), "User not found");
    }

    @Test
    void deleteUser_selfDeleteCase_returnSuccess() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();


        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        String output = userService.deleteUser(userId, principal);

        // Assert
        assertEquals("User successfully deleted", output);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_adminDeleteCase_returnSuccess() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(2L)
                .role(Role.ADMIN)
                .build();


        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        String output = userService.deleteUser(userId, principal);

        // Assert
        assertEquals("User successfully deleted", output);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_userDeleteAnotherUserCase_shouldThrow() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(2L)
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(userId, principal), "Unauthorized");
        verify(userRepository, times(0)).deleteById(any());
    }

    @Test
    void updateUser_validNewEmailAndValidAccessRequest_returnUpdatedUser() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("test1@example.com")
                .password("pass")
                .build();

        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(false);
        doAnswer(invocation -> {
            UpdateUserRequest userDto = invocation.getArgument(0);
            User mapperUser = invocation.getArgument(1);
            mapperUser.setEmail(userDto.getEmail());
            return null;
        }).when(userMapper).updateUser(request, user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class)))
                .thenAnswer(invocation -> {
                    User userForMap = invocation.getArgument(0);
                    return UserDto.builder()
                            .id(userForMap.getId())
                            .email(userForMap.getEmail())
                            .build();
                });
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);

        // Act
        UserDto output = userService.updateUser(userId, request, principal);

        // Assert
        verify(userRepository).save(savedUser.capture());
        assertEquals("encodedPass", savedUser.getValue().getPassword());
        assertEquals(request.getEmail(), output.getEmail());
    }

    @Test
    void updateUser_validNewEmailAndValidAdminAccessRequest_returnUpdatedUser() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(2L)
                .role(Role.ADMIN)
                .build();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("test1@example.com")
                .password("pass")
                .build();

        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(false);
        doAnswer(invocation -> {
            UpdateUserRequest userDto = invocation.getArgument(0);
            User mapperUser = invocation.getArgument(1);
            mapperUser.setEmail(userDto.getEmail());
            return null;
        }).when(userMapper).updateUser(request, user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class)))
                .thenAnswer(invocation -> {
                    User userForMap = invocation.getArgument(0);
                    return UserDto.builder()
                            .id(userForMap.getId())
                            .email(userForMap.getEmail())
                            .build();
                });
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);

        // Act
        UserDto output = userService.updateUser(userId, request, principal);

        // Assert
        verify(userRepository).save(savedUser.capture());
        assertEquals("encodedPass", savedUser.getValue().getPassword());
        assertEquals(request.getEmail(), output.getEmail());
    }

    @Test
    void updateUser_duplicateEmail_shouldThrow() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("test1@example.com")
                .password("pass")
                .build();

        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.updateUser(userId, request, principal), "User with this email already exist");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void updateUser_anotherUserByUser_shouldThrow() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(2L)
                .role(Role.USER)
                .build();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("test1@example.com")
                .password("pass")
                .build();

        when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(principalUser));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, request, principal), "Unauthorized");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void banUser_existingUser_returnSuccess() {
        // Arrange
        Long userId = 1L;
        List<Advert> adverts = new ArrayList<>();
        adverts.add(Advert.builder().id(1L).title("Test Advert 1").price(50.0).build());
        adverts.add(Advert.builder().id(1L).title("Test Advert 2").price(20.0).build());
        adverts.add(Advert.builder().id(1L).title("Test Advert 3").price(80.0).build());
        User user = User.builder()
                .id(userId)
                .adverts(adverts)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        // Act
        String output= userService.banUser(userId);

        // Assert
        assertEquals("Successful block", output);
        verify(userRepository).save(savedUser.capture());
        assertTrue(savedUser.getValue().getBan());
        assertTrue(savedUser.getValue().getAdverts().get(0).getBan());
        assertTrue(savedUser.getValue().getAdverts().get(1).getBan());
        assertTrue(savedUser.getValue().getAdverts().get(2).getBan());
    }
}