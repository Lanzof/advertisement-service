package com.pokotilov.finaltask.services.advert;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
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
public class AdvertService implements IAdvertService {

    private final AdvertRepository advertRepository;
    private final UserRepository userRepository;
    private final AdvertMapper advertMapper;
    private final CommentMapper commentMapper;


    public Page<OutputAdvertDto> findAdverts(String title, Double priceMax, Double priceMin, Float rating,
                                             Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
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

    @Override
    public OutputAdvertDto getAdvert(Long advertId) {

        Advert advert = getAdvertFromRepo(advertId);
        return advertMapper.toDto(advert);
    }

    @Override
    public OutputAdvertDto createAdvert(InputAdvertDto inputAdvertDto,
                                        Principal principal) {
        User user = getUserByPrincipal(principal);
        Advert advert = Advert.builder()
                .title(inputAdvertDto.getTitle())
                .description(inputAdvertDto.getDescription())
                .price(inputAdvertDto.getPrice())
                .premium(false)
                .user(user)
                .ban(false)
                .build();
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @Override
    public OutputAdvertDto updateAdvert(Long advertId, InputAdvertDto inputAdvertDto, Principal principal) {
        Advert advert = getAdvertFromRepo(advertId);
        User user = getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertMapper.updateAdvert(inputAdvertDto, advert);
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @Override
    public String deleteAdvert(Long advertId, Principal principal) {
        Advert advert = getAdvertFromRepo(advertId);
        User user = getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertRepository.deleteById(advertId);
        return "Successful deleted";
    }

    @Override
    public OutputAdvertDto banAdvert(Long id) {
        Advert advert = getAdvertFromRepo(id);
        advert.setBan(true);
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @Override
    public List<OutputCommentDto> getAdvertComments(Long advertId) {
        Advert advert = getAdvertFromRepo(advertId);
        return advert.getComments().stream().filter(comment -> !comment.getBan()).map(commentMapper::toDto).toList();
    }

    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Advert getAdvertFromRepo(Long advertId) {
        return advertRepository.findById(advertId).orElseThrow(() -> new NotFoundException("This advert doesn't exist."));
    }
}
