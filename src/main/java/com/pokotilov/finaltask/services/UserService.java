package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.entities.Vote;
import com.pokotilov.finaltask.entities.VoteID;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.mapper.UserMapper;
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
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoteRepository voteRepository;
    private final JwtService jwtService;
    private final AdvertRepository advertRepository;



    public DefaultResponse getAllUsers() {
        userRepository.findAll();
        return new DefaultResponse(
                Collections.singletonList(userRepository.findAll().stream().map(UserMapper.INSTANCE::toDto).toList()));
    }

    public DefaultResponse getAllUsers(Pageable pageable) {
        userRepository.findAll(pageable);
        return new DefaultResponse(
                Collections.singletonList(userRepository.findAll().stream().map(UserMapper.INSTANCE::toDto).toList()));
    }

    public DefaultResponse getUser(Long userId) {
        return new DefaultResponse(
                List.of(UserMapper.INSTANCE.toDto(getUserFromRepository(userId))));
    }

    public DefaultResponse deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
        return new DefaultResponse("User successfully deleted");
    }

    public DefaultResponse updateUser(Long userId, UserDto userDto) {
        User user = getUserFromRepository(userId);
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        if (userDto.getFirstName() != null) user.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());
        if (userDto.getDescription() != null) user.setDescription(userDto.getDescription());
        userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user);
        return new DefaultResponse("jwtToken of updated user");
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
        return new DefaultResponse("Successful vote");
    }

    private User getUserFromRepository(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
