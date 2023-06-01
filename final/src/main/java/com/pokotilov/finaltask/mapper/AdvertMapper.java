package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.entities.Advert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AdvertMapper {

    @Mapping(target = "userId",
            expression = "java(advert.getUser().getId())")
    @Mapping(target = "firstName",
            expression = "java(advert.getUser().getFirstName())")
    @Mapping(target = "lastName",
            expression = "java(advert.getUser().getLastName())")
    @Mapping(target = "commentsCount",
            expression = "java(advert.getComments().size())")
    @Mapping(target = "userRating",
            expression = "java(advert.getUser().getRating())")
    OutputAdvertDto toDto(Advert advert);

    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "price", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdvert(InputAdvertDto inputAdvertDto, @MappingTarget Advert advert);
}
