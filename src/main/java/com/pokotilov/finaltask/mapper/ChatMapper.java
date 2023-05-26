package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.entities.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "advertId",
            expression = "java(chat.getAdvert().getId())")
    @Mapping(target = "buyerId",
            expression = "java(chat.getBuyer().getId())")
    ChatDto toDto(Chat chat);
}
