package com.pokotilov.finaltask.services.advert;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface AdvertService {

    OutputAdvertDto getAdvert(Long advertId);

    OutputAdvertDto createAdvert(InputAdvertDto inputAdvertDto, Principal principal);

    OutputAdvertDto updateAdvert(Long advertId, InputAdvertDto inputAdvertDto, Principal principal);

    String deleteAdvert(Long advertId, Principal principal);

    OutputAdvertDto banAdvert(Long id);

    List<OutputCommentDto> getAdvertComments(Long advertId);

    Page<OutputAdvertDto> findAdverts(String title, Double priceMax, Double priceMin, Float rating, Integer pageNo, Integer pageSize, String sortField, String sortDirection);

    Advert getAdvertById(Long advertId);
}
