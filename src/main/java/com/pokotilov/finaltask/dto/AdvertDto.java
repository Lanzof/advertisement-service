package com.pokotilov.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull(message = "price cannot be empty")
    @Positive
    private Integer price;
}
