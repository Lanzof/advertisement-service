package com.pokotilov.finaltask.services.wallet;

import com.pokotilov.finaltask.dto.TransactionDto;
import com.pokotilov.finaltask.dto.WalletDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.entities.PremiumService;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface WalletService {
    @Transactional
    WalletDto createWallet(Principal principal);

    WalletDto getWallet(Principal principal);

    @Transactional
    String buyAdvert(Principal principal, Long advertId);

    @Transactional
    OutputAdvertDto buyService(Principal principal, Long serviceId, Long advertId);

    @Transactional
    WalletDto deposit(Double amount, Principal principal);

    @Transactional
    List<TransactionDto> showHistory(Principal principal);

    List<PremiumService> showServices();
}
