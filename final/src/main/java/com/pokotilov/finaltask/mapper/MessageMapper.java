package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "chatId",
            expression = "java(message.getChat().getId())")
    @Mapping(target = "senderName",
            expression = "java(message.getSender().getFirstName() + ' ' + message.getSender().getLastName())")
    MessageDto toDto(Message message);
}
