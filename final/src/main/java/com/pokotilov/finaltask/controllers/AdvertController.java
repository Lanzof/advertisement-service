package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.services.advert.IAdvertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/advert")
@RequiredArgsConstructor
@Tag(name = "Объявления", description = "Методы для работы с объявлениями.")
@Validated
public class AdvertController {

    private final IAdvertService advertService;

//    @PreAuthorize("hasAuthority('ADMIN')")
//@ApiResponses(value = { todo make all methods have a responses
//        @ApiResponse(responseCode = "200", description = "Found the book",
//                content = { @Content(mediaType = "application/json",
//                        schema = @Schema(implementation = Book.class)) }),
//        @ApiResponse(responseCode = "400", description = "Invalid id supplied",
//                content = @Content),
//        @ApiResponse(responseCode = "404", description = "Book not found",
//                content = @Content) })
    //todo if u will use sortin that way it is the documentation imp
//@Parameter(in = ParameterIn.QUERY,
//        description = "Sorting criteria in the format: property(,asc|desc). " +
//                "Default sort order is ascending. " +
//                "Multiple sort criteria are supported.",
//        name = "sort",
//        required = false,
//        array = @ArraySchema(schema = @Schema(type = "string")))

//public @ResponseBody void sort(@Parameter(hidden = true) Sort sort) {
//    @GetMapping
//    public Page<OutputAdvertDto> getAllAdverts(
//            @Parameter(description = "№ страницы.", required = true) @RequestParam int pageNo,
//            @Parameter(description = "Размер страницы.", required = true) @RequestParam int pageSize,
//            @Parameter(description = "Поле для сортировки.") @RequestParam @Nullable String sortField,
//            @Parameter(description = "Направление сортировки: asc|desc") @RequestParam @Nullable String sortDirection
//    ) {
//        return advertService.getAllAdverts(pageNo, pageSize, sortField, sortDirection);
//    }

    @GetMapping("/find")
    public Page<OutputAdvertDto> findAdverts(
            @Parameter(description = "Поисковый запрос по заголовку объявления.") @RequestParam @Nullable String title,
            @Parameter(description = "Установка фильтра максимальной цены.") @RequestParam @Nullable Double priceMax,
            @Parameter(description = "Установка фильтра минимальной цены.") @RequestParam @Nullable Double priceMin,
            @Parameter(description = "Установка фильтра минимального рейтинга.") @RequestParam @Nullable Float rating,
            @Parameter(description = "№ страницы.", required = true) @RequestParam(defaultValue = "1") @NotNull @Min(1) Integer pageNo,
            @Parameter(description = "Размер страницы.", required = true) @RequestParam(defaultValue = "10") @NotNull @Positive Integer pageSize,
            @Parameter(description = "Поле для сортировки.") @RequestParam @Nullable String sortField,
            @Parameter(description = "Направление сортировки: asc|desc") @RequestParam @Nullable String sortDirection
    ) {
        return advertService.findAdverts(title, priceMax, priceMin, rating, pageNo, pageSize, sortField, sortDirection);
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OutputAdvertDto> createAdvert(@Valid @RequestBody InputAdvertDto advert, Principal principal) {
        return ResponseEntity.ok(advertService.createAdvert(advert, principal));
    }

    @GetMapping("/{advertId}/comments")
    public List<OutputCommentDto> getAdvertComments(@PathVariable("advertId") Long id) {
        return advertService.getAdvertComments(id);
    }

    @GetMapping("/{advertId}")
    public OutputAdvertDto getAdvert(@PathVariable("advertId") Long advertId) {
        return advertService.getAdvert(advertId);
    }

    @DeleteMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteAdvert(
            @PathVariable("advertId") Long advertId, Principal principal) {
        return ResponseEntity.ok(advertService.deleteAdvert(advertId, principal));
    }

    @PutMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OutputAdvertDto> updateAdvert(
            @PathVariable("advertId") Long advertId,
            @Valid @RequestBody InputAdvertDto advert, Principal principal) {
        return ResponseEntity.ok(advertService.updateAdvert(advertId, advert, principal));
    }

    @PutMapping("/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OutputAdvertDto> banAdvert(
            @Parameter(description = "Id пользователя.") @RequestParam Long id) {
        return ResponseEntity.ok(advertService.banAdvert(id));
    }
}
