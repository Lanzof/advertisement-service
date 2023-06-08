package com.pokotilov.finaltask.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String description;
    private Float rating;
}
