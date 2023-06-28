package com.pokotilov.finaltask.dto.advert;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputFindRequest {
    @Schema(description = "Поисковый запрос по заголовку объявления.", nullable = true)
    @Nullable
    private String title;
    @Schema(description = "Установка фильтра максимальной цены.", nullable = true, type = "number", format = "double")
    @Positive
    @Nullable
    private Double priceMax;
    @Schema(description = "Установка фильтра минимальной цены.", nullable = true, type = "number", format = "double")
    @PositiveOrZero
    @Nullable
    private Double priceMin;
    @Schema(description = "Установка фильтра минимального рейтинга.", nullable = true, type = "number", format = "float")
    @Positive
    @Nullable
    private Float rating;
    @Schema(description = "№ страницы. (1..N)", type = "integer", defaultValue = "1")
    @NotNull
    @Min(1)
    private Integer pageNo;
    @Schema(description = "Размер страницы.", minimum = "1", type = "integer", defaultValue = "10")
    @NotNull
    @Positive
    private Integer pageSize;
    @Schema(description = "Поле для сортировки. Поле для сортировки по умолчанию Rating", nullable = true, allowableValues = {"ID", "TITLE", "DESCRIPTION", "DATE", "PRICE"})
    @Nullable
    private SortField sortField;
    @Schema(description = "Направление сортировки: asc|desc. По умолчанию desc", nullable = true, allowableValues = {"asc", "desc"})
    @Nullable
    private String sortDirection;
}
