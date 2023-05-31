package com.pokotilov.finaltask.dto.advert;

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
public class InputAdvertDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull(message = "Price can't be null.")
    @Positive(message = "Price should be positive.")
    private Double price;
}
