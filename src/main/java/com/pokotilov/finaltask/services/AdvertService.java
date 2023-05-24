package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final UserRepository userRepository;
    private final AdvertMapper advertMapper;

    public DefaultResponse getAllAdverts() {
        List<OutputAdvertDto> outputAdvertDtos = advertRepository.findAll().stream()
                .map(advertMapper::toDto).toList();
        return new DefaultResponse(Collections.singletonList(outputAdvertDtos));
    }

    public DefaultResponse getAdvert(Long advertId) {
        Advert advert = advertRepository.getReferenceById(advertId);
        OutputAdvertDto dto = advertMapper.toDto(advert);
        return new DefaultResponse(List.of(dto));
    }

    public DefaultResponse createAdvert(InputAdvertDto inputAdvertDto, Principal principal) {
        User user = getUserByPrincipal(principal);
        Advert advert = Advert.builder()
                .title(inputAdvertDto.getTitle())
                .description(inputAdvertDto.getDescription())
                .date(LocalDateTime.now())
                .price(inputAdvertDto.getPrice())
                .premium(false)
                .user(user)
                .ban(false)
                .build();
        advertRepository.save(advert);
        return new DefaultResponse("Successful add");
    }

    public DefaultResponse updateAdvert(Long advertId, InputAdvertDto inputAdvertDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(advertId);
        User user = getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertMapper.updateAdvert(inputAdvertDto, advert);
        advertRepository.save(advert);
        return new DefaultResponse("Successful edited");
    }

    public DefaultResponse deleteAdvert(Long advertId, Principal principal) {
        Advert advert = advertRepository.getReferenceById(advertId);
        User user = getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertRepository.deleteById(advertId);
        return new DefaultResponse("Successful deleted");
    }

    public DefaultResponse banAdvert(Long id) {
        Advert advert = advertRepository.getReferenceById(id);
        advert.setBan(true);
        advertRepository.save(advert);
        return new DefaultResponse("Successful block");
    }

    public DefaultResponse getAdvertComments(Long advertId) {
        Advert advert = advertRepository.getReferenceById(advertId);
        List<Comment> comments = advert.getComments();
        return new DefaultResponse(Collections.singletonList(comments));
    }

    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
