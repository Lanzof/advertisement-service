package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.AdvertDto;
import com.pokotilov.finaltask.services.AdvertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
//@GetMapping("/sort")

//public @ResponseBody void sort(@Parameter(hidden = true) Sort sort) {
    @GetMapping
    @Operation()
    public ResponseEntity<?> getAllAdverts() {
        return ResponseEntity.ok(advertService.getAllAdverts().getAdverts());
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createAdvert(@RequestBody AdvertDto advert, Principal principal) {
        return ResponseEntity.ok(advertService.createAdvert(advert, principal).getMessage());
    }

    @GetMapping("/{advertId}/comments")
    public ResponseEntity<?> getAdvertComments(@PathVariable("advertId") Long id) {
        return ResponseEntity.ok(advertService.getAdvertComments(id).getMessage());
    }

    @GetMapping("/{advertId}")
    public ResponseEntity<?> getSingleAdvert(@PathVariable("advertId") Long advertId) {
        return ResponseEntity.ok(advertService.getAdvert(advertId).getAdvertDtos());
    }

    @DeleteMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteAdvert(@PathVariable("advertId") Long advertId) {
        return ResponseEntity.ok(advertService.deleteAdvert(advertId).getMessage());
    }

    @PutMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateAdvert(@PathVariable("advertId") Long advertId, @RequestBody AdvertDto advert, Principal principal) {
        return ResponseEntity.ok(advertService.updateAdvert(advertId, advert, principal).getMessage());
    }

    @PutMapping("/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateAdvert(@RequestBody Long id) {
        return ResponseEntity.ok(advertService.banAdvert(id).getMessage());
    }
}
