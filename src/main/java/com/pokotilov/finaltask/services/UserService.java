package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.SelfVoteException;
import com.pokotilov.finaltask.exceptions.UserAlreadyExistException;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.mapper.UserMapper;
import com.pokotilov.finaltask.mapper.VoteMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoteRepository voteRepository;
    private final AdvertRepository advertRepository;
    private final UserMapper userMapper;
    private final VoteMapper voteMapper;

    private static final String USER_NOT_FOUND = "User not found";



    public DefaultResponse getAllUsers() { //todo to delete?
        return new DefaultResponse(
                Collections.singletonList(userRepository.findAll().stream().map(userMapper::toDto).toList()));
    }

    public DefaultResponse getAllUsers(Pageable pageable) { //todo add default sort
        return new DefaultResponse(
                Collections.singletonList(userRepository.findAll(pageable).stream().map(userMapper::toDto).toList()));
    }

    public DefaultResponse getUser(Long userId) {
        return new DefaultResponse(
                List.of(userMapper.toDto(getUserById(userId))));
    }

    public DefaultResponse deleteUser(Long userId, Principal principal) {
        User user = getUserById(userId);
        User requester = getUserByPrincipal(principal);
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        userRepository.deleteById(userId);
        return new DefaultResponse("User successfully deleted");
    }

    public DefaultResponse updateUser(Long userId, UpdateUserRequest updateUserRequest, Principal principal) {
        User user = getUserById(userId);
        User requester = getUserByPrincipal(principal);
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new UserAlreadyExistException("User with this email already exist");
        }
        userMapper.updateUser(updateUserRequest, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new DefaultResponse("Successful editing");
    }

    public DefaultResponse banUser(Long id) {
        User user = getUserById(id);
        user.setBan(true);
        userRepository.save(user);
        return new DefaultResponse("Successful block");
    }

    public DefaultResponse voteUser(VoteDto voteDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(voteDto.getAdvertId());
        User user = advert.getUser();
        User author = getUserByPrincipal(principal);
        if (user.getId().equals(author.getId())) {
            throw new SelfVoteException("You can't vote for yourself");
        }
        Vote vote = Vote.builder()
                .voteID(VoteID.builder()
                        .authorId(author.getId())
                        .advertId(advert.getId())
                        .build())
                .date(LocalDateTime.now())
                .vote(voteDto.getVote())
                .author(author)
                .advert(advert)
                .build();
        voteRepository.save(vote);
        return new DefaultResponse("Successful vote");
    }

    private User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }
}
