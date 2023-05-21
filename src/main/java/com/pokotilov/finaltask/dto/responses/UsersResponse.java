package com.pokotilov.finaltask.dto.responses;

import com.pokotilov.finaltask.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    private List<UserDto> userDtos;
    private String message;
}
