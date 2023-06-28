package com.pokotilov.finaltask.services.advert;

import com.pokotilov.finaltask.aop.LogExecution;
import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.InputFindRequest;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.exceptions.UnprocessableEntityException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.mapper.CommentMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.VoteRepository;
import com.pokotilov.finaltask.services.user.UserService;
import com.pokotilov.finaltask.util.Spec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AdvertServiceImpl implements AdvertService {

    private final UserService userService;
    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final CommentMapper commentMapper;
    private final VoteRepository voteRepository;

    @LogExecution
    public Page<OutputAdvertDto> findAdverts(InputFindRequest request) {
        Spec spec = Spec.builder()
                .title(request.getTitle())
                .minPrice(request.getPriceMin())
                .maxPrice(request.getPriceMax())
                .rating(request.getRating())
                .sortFieldInput(request.getSortField())
                .sortDirection(request.getSortDirection())
                .build();
        Pageable pageable = PageRequest.of(request.getPageNo() - 1, request.getPageSize());
        Page<Advert> page = advertRepository.findAll(spec, pageable);
        return page.map(advertMapper::toDto);
    }

    @LogExecution
    public OutputAdvertDto getAdvert(Long advertId) {

        Advert advert = getAdvertById(advertId);
        return advertMapper.toDto(advert);
    }

    @LogExecution
    public OutputAdvertDto createAdvert(InputAdvertDto inputAdvertDto, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Advert advert = Advert.builder()
                .title(inputAdvertDto.getTitle())
                .description(inputAdvertDto.getDescription())
                .price(inputAdvertDto.getPrice())
                .premiumEnd(LocalDate.now().minusDays(1L))
                .user(user)
                .ban(false)
                .build();
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @LogExecution
    public OutputAdvertDto updateAdvert(Long advertId, InputAdvertDto inputAdvertDto, Principal principal) {
        Advert advert = getAdvertById(advertId);
        User user = userService.getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertMapper.updateAdvert(inputAdvertDto, advert);
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @LogExecution
    public String deleteAdvert(Long advertId, Principal principal) {
        Advert advert = getAdvertById(advertId);
        User user = userService.getUserByPrincipal(principal);
        if ((user.getRole() != Role.ADMIN) && (!advert.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Unauthorized");
        }
        advertRepository.deleteById(advertId);
        return "Successful deleted";
    }

    @LogExecution
    public OutputAdvertDto banAdvert(Long id) {
        Advert advert = getAdvertById(id);
        advert.setBan(true);
        return advertMapper.toDto(advertRepository.save(advert));
    }

    @LogExecution
    public String voteAdvert(VoteDto voteDto, Principal principal) {
        Advert advert = getAdvertById(voteDto.getAdvertId());
        User user = advert.getUser();
        User author = userService.getUserByPrincipal(principal);
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

    @LogExecution
    public List<OutputCommentDto> getAdvertComments(Long advertId) {
        Advert advert = getAdvertById(advertId);
        return advert.getComments().stream()
                .filter(comment -> !comment.getBan()).map(commentMapper::toDto).toList();
    }

    public Advert getAdvertById(Long advertId) {
        return advertRepository.findById(advertId)
                .orElseThrow(() -> new NotFoundException("This advert doesn't exist."));
    }
}
