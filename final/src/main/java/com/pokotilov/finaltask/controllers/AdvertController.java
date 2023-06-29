package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.InputFindRequest;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.entities.PremiumService;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.wallet.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.*;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/adverts")
@RequiredArgsConstructor
@Tag(name = "Объявления", description = "Методы для работы с объявлениями.")
@Validated
public class AdvertController {

    private final AdvertService advertService;
    private final WalletService walletService;
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @GetMapping("/find")
    public Page<OutputAdvertDto> findAdverts(@Valid @ParameterObject InputFindRequest request) {
        return advertService.findAdverts(request);
    }

    @GetMapping("/{advertId}")
    public OutputAdvertDto getAdvert(@PathVariable("advertId") @Positive Long advertId) {
        return advertService.getAdvert(advertId);
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public OutputAdvertDto createAdvert(@Valid @RequestBody InputAdvertDto advert, Principal principal) {
        return advertService.createAdvert(advert, principal);
    }

    @DeleteMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> deleteAdvert(
            @PathVariable("advertId") @Positive Long advertId, Principal principal) {
        return ResponseEntity.ok(advertService.deleteAdvert(advertId, principal));
    }

    @PutMapping("/{advertId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public OutputAdvertDto updateAdvert(
            @PathVariable("advertId") @Positive Long advertId,
            @Valid @RequestBody InputAdvertDto advert, Principal principal) {
        return advertService.updateAdvert(advertId, advert, principal);
    }

    @PostMapping("/{advertId}/vote")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> voteUser(@Parameter(description = "Id объявления.") @PathVariable("advertId") @Positive Long id,
                                           @RequestParam Integer vote , Principal principal) {
        VoteDto voteDto = new VoteDto(vote, id);
        Set<ConstraintViolation<VoteDto>> violations = validator.validate(voteDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ResponseEntity.ok().body(advertService.voteAdvert(voteDto, principal));
    }

    @PostMapping("/{advertId}/buy")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> buyAdvert(
            @Parameter(description = "Id объявления.", required = true) @PathVariable("advertId") @Positive Long advertId,
            Principal principal) {
        return ResponseEntity.ok(walletService.buyAdvert(principal, advertId));
    }

    @GetMapping("/services")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public List<PremiumService> showServices(){
        return walletService.showServices();
    }

    @PostMapping("/{advertId}/services/{serviceId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public OutputAdvertDto buyService(
            @Parameter(description = "Id сервиса.", required = true) @PathVariable("serviceId") @Positive Long serviceId,
            @Parameter(description = "Id объявления.", required = true) @PathVariable("advertId") @Positive Long advertId,
            Principal principal) {
        return walletService.buyService(principal, serviceId, advertId);
    }

    @PutMapping("/{advertId}/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @Secured({ "ROLE_ADMIN" })
    public OutputAdvertDto banAdvert(
            @Parameter(description = "Id объявления.") @PathVariable("advertId") @Positive Long id) {
        return advertService.banAdvert(id);
    }
}
