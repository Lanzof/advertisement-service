package com.pokotilov.finaltask.dto.advert;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class SearchAdvertDto {
    @Nullable
    private String title;
    @Nullable
    private Double priceMax;
    @Nullable
    private Double priceMin;
    @Nullable
    private Float rating;
}
