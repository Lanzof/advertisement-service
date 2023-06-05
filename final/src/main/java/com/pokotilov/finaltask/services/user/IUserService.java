package com.pokotilov.finaltask.services.user;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface IUserService {
    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto getUser(Long userId);

    String deleteUser(Long userId, Principal principal);

    String updateUser(Long userId, UpdateUserRequest updateUserRequest, Principal principal);

    String banUser(Long id);

    String voteUser(VoteDto voteDto, Principal principal);
}
