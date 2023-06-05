package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.entities.PremiumService;
import com.pokotilov.finaltask.entities.Transaction;
import com.pokotilov.finaltask.services.wallet.IWalletService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Кошелёк", description = "Методы для работы с кошельком.")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final IWalletService IWalletService;

    @PostMapping
    public ResponseEntity<String> createWallet(Principal principal) {
        return ResponseEntity.ok(IWalletService.createWallet(principal));
    }

    @GetMapping
    public ResponseEntity<String> getWallet(Principal principal) {
        return ResponseEntity.ok(IWalletService.getWallet(principal));
    }

    @GetMapping("/advert")
    public ResponseEntity<String> buyAdvert(Principal principal,
            @Parameter(description = "Id объявления.", required = true) @RequestParam Long advertId) {
        return ResponseEntity.ok(IWalletService.buyAdvert(principal, advertId));
    }

    @GetMapping("/service")
    public ResponseEntity<String> buyService(Principal principal,
            @Parameter(description = "Id сервиса.", required = true) @RequestParam Long serviceId,
            @Parameter(description = "Id объявления.", required = true) @RequestParam Long advertId) {
        return ResponseEntity.ok(IWalletService.buyService(principal, serviceId, advertId));
    }

    @PostMapping("/add")
    public ResponseEntity<String> makeDeposit(
            @Parameter(description = "Количество денег.", required = true) @RequestParam Double amount,
            Principal principal) {
        return ResponseEntity.ok().body(IWalletService.deposit(amount, principal));
    }

    @GetMapping("/history")
    public List<Transaction> showTransactionHistory(Principal principal) {
        return IWalletService.showHistory(principal);
    }

    @GetMapping("/services")
    public List<PremiumService> showServices(){
        return IWalletService.showServices();
    }
}
