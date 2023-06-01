package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.UnprocessableEntityException;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.UserMapper;
import com.pokotilov.finaltask.mapper.VoteMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

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

    public Page<UserDto> getAllUsers(Pageable pageable) { //todo add default sort
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    public UserDto getUser(Long userId) {
        return userMapper.toDto(getUserById(userId));
    }

    public String deleteUser(Long userId, Principal principal) {
        User user = getUserById(userId);
        User requester = getUserByPrincipal(principal);
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        userRepository.deleteById(userId);
        return "User successfully deleted";
    }

    public String updateUser(Long userId, UpdateUserRequest updateUserRequest, Principal principal) {
        User user = getUserById(userId);
        User requester = getUserByPrincipal(principal);
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new BadRequestException("User with this email already exist");
        }
        userMapper.updateUser(updateUserRequest, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "Successful editing";
    }

    public String banUser(Long id) {
        User user = getUserById(id);
        user.setBan(true);
        user.getAdverts().forEach(advert -> advert.setBan(true));
        userRepository.save(user);
        return "Successful block";
    }

    public String voteUser(VoteDto voteDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(voteDto.getAdvertId());
        User user = advert.getUser();
        User author = getUserByPrincipal(principal);
        if (user.getId().equals(author.getId())) {
            throw new UnprocessableEntityException("You can't vote for yourself");
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
        return "Successful vote";
    }

    private User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }
}