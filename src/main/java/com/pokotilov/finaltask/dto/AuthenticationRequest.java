package com.pokotilov.finaltask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "someEmail@gmail.com")
    @NotBlank(message = "email is blank")
    @Pattern(regexp = "^[\\w!#$%&amp;'*+/=?`{|}~^\\-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^\\-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "wrong email")
    private String email;
    @Schema(example = "YourPassword23[]")
    @NotBlank(message = "password is blank")
    @Size(min = 8, max = 255, message = "password should have 8 symbols at least, contain one upper and one lower case latin symbol and special character")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~]).{8,}$",
            message = "password should have 8 symbols at least, contain one upper and one lower case latin symbol and special character")
    private String password;
}
