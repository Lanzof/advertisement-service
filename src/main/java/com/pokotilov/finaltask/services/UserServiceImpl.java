package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.entities.Vote;
import com.pokotilov.finaltask.entities.VoteID;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.repositories.VoteRepository;
import com.pokotilov.finaltask.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoteRepository voteRepository;
    private final JwtService jwtService;
    private final AdvertRepository advertRepository;



    public DefaultResponse getAllUsers() {
        userRepository.findAll();
        return DefaultResponse.builder()
                .list(Collections.singletonList(userRepository.findAll().stream().map(UserDto::toDto).toList()))
                .build();
    }

    public DefaultResponse getAllUsers(Pageable pageable) {
        userRepository.findAll(pageable);
        return DefaultResponse.builder()
                .list(Collections.singletonList(userRepository.findAll().stream().map(UserDto::toDto).toList()))
                .build();
    }

    public DefaultResponse getUser(Long userId) {
        //todo add permissions?
        return DefaultResponse.builder()
                .list(List.of(
                        UserDto.toDto(userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found")))))
                .build();
    }

    public DefaultResponse deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
        return DefaultResponse.builder()
                .message("User successfully deleted")
                .build();
    }

    public DefaultResponse updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        if (userDto.getFirstName() != null) user.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());
        if (userDto.getDescription() != null) user.setDescription(userDto.getDescription());
        userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user);
        return DefaultResponse.builder()
                .message("jwtToken of updated user")
                .build();
    }

    public DefaultResponse blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setBan(true);
        userRepository.save(user);
        return DefaultResponse.builder()
                .message("Successful block")
                .build();
    }

    public DefaultResponse voteUser(VoteDto voteDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(voteDto.getAdvert_id());
        User user = advert.getUser();
        User author = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
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
        return DefaultResponse.builder()
                .message("Successful vote")
                .build();
    }
}
