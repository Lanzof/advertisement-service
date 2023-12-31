package com.pokotilov.finaltask.services.user;

import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import com.pokotilov.finaltask.entities.User;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface UserService {
    UserDto getUser(Long userId);

    String deleteUser(Long userId, Principal principal);

    UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest, Principal principal);

    String banUser(Long id);

    User getUserById(Long userId);

    User getUserByPrincipal(Principal principal);

    Page<UserDto> getAllUsers(Integer pageNo, Integer pageSize);
}
