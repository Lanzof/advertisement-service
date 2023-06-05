package com.pokotilov.finaltask.services.wallet;

import com.pokotilov.finaltask.entities.PremiumService;
import com.pokotilov.finaltask.entities.Transaction;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface IWalletService {
    @Transactional
    String createWallet(Principal principal);

    String getWallet(Principal principal);

    @Transactional
    String buyAdvert(Principal principal, Long advertId);

    @Transactional
    String buyService(Principal principal, Long serviceId, Long advertId);

    @Transactional
    String deposit(Double amount, Principal principal);

    @Transactional
    List<Transaction> showHistory(Principal principal);

    List<PremiumService> showServices();
}