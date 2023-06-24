package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.TransactionDto;
import com.pokotilov.finaltask.dto.WalletDto;
import com.pokotilov.finaltask.services.wallet.WalletService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Кошелёк", description = "Методы для работы с кошельком.")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public WalletDto createWallet(Principal principal) {
        return walletService.createWallet(principal);
    }

    @GetMapping
    public WalletDto getWallet(Principal principal) {
        return walletService.getWallet(principal);
    }

    @PostMapping("/add")
    public WalletDto makeDeposit(
            @Parameter(description = "Количество денег.", required = true) @Positive @RequestParam Double amount,
            Principal principal) {
        return walletService.deposit(amount, principal);
    }

    @GetMapping("/history")
    public List<TransactionDto> showTransactionHistory(Principal principal) {
        return walletService.showHistory(principal);
    }
}
