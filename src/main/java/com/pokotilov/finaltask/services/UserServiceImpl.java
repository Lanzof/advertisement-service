package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.dto.responses.UsersResponse;
import com.pokotilov.finaltask.dto.VoteDto;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoteRepository voteRepository;
    private final JwtService jwtService;
    private final AdvertRepository advertRepository;



    public UsersResponse getAllUsers() {
        userRepository.findAll();
        return UsersResponse.builder()
                .userDtos(userRepository.findAll().stream().map(UserDto::toDto).collect(Collectors.toList()))
                .build();
    }

    public UsersResponse getAllUsers(Pageable pageable) {
        userRepository.findAll(pageable);
        return UsersResponse.builder()
                .userDtos(userRepository.findAll().stream().map(UserDto::toDto).collect(Collectors.toList()))
                .build();
    }

    public UsersResponse getUser(Long userId) {
        //todo add permissions?
        return UsersResponse.builder()
                .userDtos(List.of(
                        UserDto.toDto(userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found")))))
                .build();
    }

    public UsersResponse deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
        return UsersResponse.builder()
                .message("User successfully deleted")
                .build();
    }

    public UsersResponse updateUser(Long id, UserDto userDto) {
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
        return UsersResponse.builder()
                .message("jwtToken of updated user")
                .build();
    }

    public UsersResponse blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setBan(true);
        userRepository.save(user);
        return UsersResponse.builder()
                .message("Successful block")
                .build();
    }

    public UsersResponse voteUser(VoteDto voteDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(voteDto.getAdvert_id());
        User user = advert.getUser();
        User author = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.equals(author)) {
            throw new UserNotFoundException("You can't vote for yourself");//todo remade with 422
        }
        Vote vote = Vote.builder()
                .voteID(VoteID.builder()
                        .author(author)
                        .advert(advert)
                        .build())
                .date(LocalDateTime.now())
                .vote(voteDto.getVote())
                .build();
        voteRepository.save(vote);
        return UsersResponse.builder()
                .message("Successful vote")
                .build();
    }
}
