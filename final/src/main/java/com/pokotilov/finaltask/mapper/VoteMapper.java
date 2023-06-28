package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.entities.Vote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    VoteDto toDto(Vote vote);
}
