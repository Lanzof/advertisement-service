package com.pokotilov.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
    private String description;
    private Float rating;

}
