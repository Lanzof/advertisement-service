package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto toDto(User user);
    User toUser(UserDto userDto);
}
