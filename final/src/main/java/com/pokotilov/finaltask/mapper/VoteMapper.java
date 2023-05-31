package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.entities.Vote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoteMapper {
//    @Mapping(target = "advertId",
//            expression = "java(message.getAdvert().getId())")
    VoteDto toDto(Vote vote);
}
