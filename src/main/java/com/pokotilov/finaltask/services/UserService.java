package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.UserAlreadyExist;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.mapper.UserMapper;
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

    private static final String USER_NOT_FOUND = "User not found";



    public DefaultResponse getAllUsers() {
        return new DefaultResponse(
                Collections.singletonList(userRepository.findAll().stream().map(userMapper::toDto).toList()));
    }

    public DefaultResponse getAllUsers(Pageable pageable) {
        return new DefaultResponse(
                Collections.singletonList(userRepository.findAll(pageable).stream().map(userMapper::toDto).toList()));
    }

    public DefaultResponse getUser(Long userId) {
        return new DefaultResponse(
                List.of(userMapper.toDto(getUserFromRepository(userId))));
    }

    public DefaultResponse deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return new DefaultResponse("User successfully deleted");
    }

    public DefaultResponse updateUser(Long userId, UserDto userDto, Principal principal) {
        User user = getUserFromRepository(userId);
        User requester = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        if ((requester.getRole() != Role.ADMIN) && (!user.getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExist("User with this email already exist");
        }
        userMapper.updateUser(userDto, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new DefaultResponse("Successful editing");
    }

    public DefaultResponse blockUser(Long id) {
        User user = getUserFromRepository(id);
        user.setBan(true);
        userRepository.save(user);
        return new DefaultResponse("Successful block");
    }

    public DefaultResponse voteUser(VoteDto voteDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(voteDto.getAdvert_id());
        User user = advert.getUser();
        User author = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        if (user.equals(author)) {
            throw new UserNotFoundException("You can't vote for yourself");//todo remade with 422
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

    private User getUserFromRepository(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }
}
