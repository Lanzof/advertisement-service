package com.pokotilov.finaltask.services.user;

import com.pokotilov.finaltask.aop.LogExecution;
import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.UserMapper;
import com.pokotilov.finaltask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String USER_NOT_FOUND = "User not found";

    @Override
    @LogExecution
    public Page<UserDto> getAllUsers(Integer pageNo, Integer pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("rating"));
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    @LogExecution
    public UserDto getUser(Long userId) {
        return userMapper.toDto(getUserById(userId));
    }

    @Override
    @LogExecution
    public String deleteUser(Long userId, Principal principal) {
        User user = getUserById(userId);
        User requester = getUserByPrincipal(principal);
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        userRepository.deleteById(userId);
        return "User successfully deleted";
    }

    @Override
    @LogExecution
    public UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest, Principal principal) {
        User user = getUserById(userId);
        User requester = getUserByPrincipal(principal);
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        User duplicateCheck = userRepository.findByEmail(updateUserRequest.getEmail()).orElse(null);
        if (duplicateCheck != null && !duplicateCheck.getId().equals(requester.getId())) {
            throw new BadRequestException("User with this email already exist");
        }
        userMapper.updateUser(updateUserRequest, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @LogExecution
    public String banUser(Long id) {
        User user = getUserById(id);
        user.setBan(true);
        user.getAdverts().forEach(advert -> advert.setBan(true));
        userRepository.save(user);
        return "Successful block";
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }
}
