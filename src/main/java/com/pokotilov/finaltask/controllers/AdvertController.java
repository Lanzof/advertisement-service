package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.services.AdvertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/advert")
@RequiredArgsConstructor
@Tag(name = "Объявления", description = "Методы для работы с объявлениями.")
@SecurityRequirement(name = "bearerAuth")
public class AdvertController {

    private final AdvertService advertService;

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
    @GetMapping
    public Page<OutputAdvertDto> getAllAdverts(
            @Parameter(description = "№ страницы.", required = true) @RequestParam int pageNo,
            @Parameter(description = "Размер страницы.", required = true) @RequestParam int pageSize,
            @Parameter(description = "Поле для сортировки.") @RequestParam @Nullable String sortField,
            @Parameter(description = "Направление сортировки: asc|desc") @RequestParam @Nullable String sortDirection
    ) {
        return advertService.getAllAdverts(pageNo, pageSize, sortField, sortDirection);
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public String createAdvert(@Valid @RequestBody InputAdvertDto advert, Principal principal) {
        return advertService.createAdvert(advert, principal);
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
    public String deleteAdvert(@PathVariable("advertId") Long advertId, Principal principal) {
        return advertService.deleteAdvert(advertId, principal);
    }

    @PutMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public String updateAdvert(@PathVariable("advertId") Long advertId, @Valid @RequestBody InputAdvertDto advert, Principal principal) {
        return advertService.updateAdvert(advertId, advert, principal);
    }

    @PutMapping("/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    public String banAdvert(
            @Parameter(description = "Id пользователя.") @RequestParam Long id) {
        return advertService.banAdvert(id);
    }
}
