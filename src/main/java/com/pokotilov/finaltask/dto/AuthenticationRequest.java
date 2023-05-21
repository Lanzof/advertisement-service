package com.pokotilov.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "email is blank")
    @Pattern(regexp = "^[\\w!#$%&amp;'*+/=?`{|}~^\\-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^\\-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "wrong email")
    private String email;
    @NotBlank(message = "password is blank")
    @Size(min = 8, max = 255, message = "password should have 8 symbols at least, contain one upper and one lower case latin symbol and special character")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~]).{8,}$",
            message = "password should have 8 symbols at least, contain one upper and one lower case latin symbol and special character")
    private String password;
}
