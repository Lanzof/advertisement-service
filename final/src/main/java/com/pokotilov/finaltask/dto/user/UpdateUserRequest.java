package com.pokotilov.finaltask.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @Schema(example = "someEmail@gmail.com")
    @NotBlank(message = "email is blank")
    @Email(regexp = "^[\\w!#$%&amp;'*+/=?`{|}~^\\-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^\\-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "wrong email")
    private String email;
    @Schema(example = "YourPassword23[]", description = "Пароль должен быть минимум 8 символов и содержать как минимум 1 заглавную и строчную латинскую букву и специальный символ")
    @NotBlank(message = "password is blank")
    @Size(min = 8, max = 255, message = "password should have 8 symbols at least")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~]).{8,}$",
            message = "password should contain one upper and one lower case latin symbol and special character")
    private String password;
    @Schema(example = "Pablo")
    @NotBlank(message = "first name is blank")
    @Pattern(regexp = "[a-zA-ZА-Яа-я \\-]*$")
    private String firstName;
    @Schema(example = "Ganacci")
    @NotBlank(message = "last name is blank")
    @Pattern(regexp = "[a-zA-ZА-Яа-я \\-]*$")
    private String lastName;
    @Schema(example = "+77894561212")
    @NotBlank(message = "phone is blank")
    @Pattern(regexp = "^\\+?\\d*$",
            message = "phone number should be +7(xxx)xxxxxxx")
    private String phone;
    @Nullable
    private String description;
}
