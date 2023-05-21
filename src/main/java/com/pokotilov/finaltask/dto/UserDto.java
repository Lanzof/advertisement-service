package com.pokotilov.finaltask.dto;

import com.pokotilov.finaltask.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String email;
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
    private String description;

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .description(user.getDescription())
                .build();
    }
}
