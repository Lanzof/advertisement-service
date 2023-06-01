package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.advert.SearchAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.mapper.CommentMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.util.Spec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final UserRepository userRepository;
    private final AdvertMapper advertMapper;
    private final CommentMapper commentMapper;

//    public Page<OutputAdvertDto> getAllAdverts(int pageNo, int pageSize, String sortField, String sortDirection) {
//        Pageable pageable = createPageable(pageNo, pageSize, sortField, sortDirection);
//        Page<Advert> page = advertRepository.getAdvertsByDefaultQuery(pageable);
//        return page.map(advertMapper::toDto);
//    }

    public Page<OutputAdvertDto> findAdverts(String title, Double priceMax, Double priceMin, Float rating,
                                             int pageNo, int pageSize, String sortField, String sortDirection) {
        Spec spec = Spec.builder()
                .title(title)
                .minPrice(priceMin)
                .maxPrice(priceMax)
                .rating(rating)
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Advert> page = advertRepository.findAll(spec, pageable);
        return page.map(advertMapper::toDto);
    }

    public OutputAdvertDto getAdvert(Long advertId) {
        Advert advert = advertRepository.getReferenceById(advertId);
        return advertMapper.toDto(advert);
    }

    public String createAdvert(InputAdvertDto inputAdvertDto, Principal principal) {
        User user = getUserByPrincipal(principal);
        Advert advert = Advert.builder()
                .title(inputAdvertDto.getTitle())
                .description(inputAdvertDto.getDescription())
                .price(inputAdvertDto.getPrice())
                .premium(false)
                .user(user)
                .ban(false)
                .build();
        advertRepository.save(advert);
        return "Successful add";
    }

    public String updateAdvert(Long advertId, InputAdvertDto inputAdvertDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(advertId);
        User user = getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertMapper.updateAdvert(inputAdvertDto, advert);
        advertRepository.save(advert);
        return "Successful edited";
    }

    public String deleteAdvert(Long advertId, Principal principal) {
        Advert advert = advertRepository.getReferenceById(advertId);
        User user = getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertRepository.deleteById(advertId);
        return "Successful deleted";
    }

    public String banAdvert(Long id) {
        Advert advert = advertRepository.getReferenceById(id);
        advert.setBan(true);
        advertRepository.save(advert);
        return "Successful block";
    }

    public List<OutputCommentDto> getAdvertComments(Long advertId) {
        Advert advert = advertRepository.getReferenceById(advertId);
        return advert.getComments().stream().filter(comment -> !comment.getBan()).map(commentMapper::toDto).toList();
    }

    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
