package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.AdvertDto;
import com.pokotilov.finaltask.dto.DefaultResponse;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
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

    public DefaultResponse getAllAdverts() {
        List<AdvertDto> advertDtos = advertRepository.findAll().stream()
                .map(advert -> AdvertDto.builder()
                        .title(advert.getTitle())
                        .description(advert.getDescription())
                        .price(advert.getPrice())
                        .build()).toList();
        return new DefaultResponse(Collections.singletonList(advertDtos));
    }

    public DefaultResponse getAdvert(Long advertId) {
        Advert advert = advertRepository.getReferenceById(advertId);
        AdvertDto dto = AdvertDto.builder()
                .title(advert.getTitle())
                .description(advert.getDescription())
                .price(advert.getPrice())
                .build();
        return new DefaultResponse(List.of(dto));
    }

    public DefaultResponse deleteAdvert(Long advertId) {
        advertRepository.deleteById(advertId);
        return new DefaultResponse("Successful deleted");
    }

    public DefaultResponse createAdvert(AdvertDto advertDto, Principal principal) {
        User user =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Advert advert = Advert.builder()
                .title(advertDto.getTitle())
                .description(advertDto.getDescription())
                .date(LocalDateTime.now())
                .price(advertDto.getPrice())
                .premium(false)
                .user(user)
                .ban(false)
                .build();
        advertRepository.save(advert);
        return new DefaultResponse("Successful add");
    }

    public DefaultResponse updateAdvert(Long advertId, AdvertDto advertDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(advertId);
        User user =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!advert.getUser().equals(user)) {
            throw new AccessDeniedException("unauthorized");//todo remade with 401
        }
        advert.setTitle(advertDto.getTitle());
        advert.setDescription(advertDto.getDescription());
        advert.setPrice(advertDto.getPrice());
        advertRepository.save(advert);
        return new DefaultResponse("Successful edited");
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
}
