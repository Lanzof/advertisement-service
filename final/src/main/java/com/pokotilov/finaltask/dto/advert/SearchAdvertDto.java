package com.pokotilov.finaltask.dto.advert;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class SearchAdvertDto {
    @Nullable
    private String title;
    @Nullable
    @PositiveOrZero
    private Double priceMin;
    @Nullable
    @PositiveOrZero
    private Double priceMax;
    @Nullable
    private Float rating;
}
