package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.entities.User;

//@Mapper
public interface UserMapper {
    UserDto toDto(User user);
    User toUser(UserDto userDto);
}
