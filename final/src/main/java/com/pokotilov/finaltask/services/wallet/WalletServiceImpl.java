package com.pokotilov.finaltask.services.wallet;

import com.pokotilov.finaltask.aop.LogExecution;
import com.pokotilov.finaltask.dto.TransactionDto;
import com.pokotilov.finaltask.dto.WalletDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.ConflictException;
import com.pokotilov.finaltask.exceptions.ExpectationFailedException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.mapper.TransactionMapper;
import com.pokotilov.finaltask.mapper.WalletMapper;
import com.pokotilov.finaltask.repositories.ServiceRepository;
import com.pokotilov.finaltask.repositories.TransactionRepository;
import com.pokotilov.finaltask.repositories.WalletRepository;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserService userService;
    private final AdvertService advertService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ServiceRepository serviceRepository;
    private final WalletMapper walletMapper;
    private final AdvertMapper advertMapper;
    private final TransactionMapper transactionMapper;


    @Override
    @LogExecution
    public WalletDto createWallet(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        if (walletRepository.existsById(user.getId())) {
            throw new ConflictException("Wallet already exist");
        }
        Wallet wallet = new Wallet();
        wallet.setBalance(0.00);
        wallet.setUser(user);

        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Override
    @LogExecution
    public WalletDto getWallet(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        return walletMapper.toDto(wallet);
    }

    @Override
    @LogExecution
    @Transactional
    public String buyAdvert(Principal principal, Long advertId) {
        User user = userService.getUserByPrincipal(principal);
        Wallet buyerWallet = getWalletFromUser(user);
        Advert advert = advertService.getAdvertById(advertId);
        if (Boolean.TRUE.equals(advert.getBan())) {
            throw new ConflictException("This advert is banned");
        }
        if (user.getId().equals(advert.getUser().getId())) {
            throw new ExpectationFailedException("You cannot buy your own ad");
        }
        Wallet advertOwnerWallet = walletRepository.findById(advert.getUser().getId())
                .orElseThrow(() -> new ConflictException("The seller does not have a wallet"));
        if (buyerWallet.getBalance() - advert.getPrice() < 0) {
            throw new BadRequestException("Not enough money in the account wallet");
        }
        buyerWallet.setBalance(buyerWallet.getBalance() - advert.getPrice());
        advertOwnerWallet.setBalance(advertOwnerWallet.getBalance() + advert.getPrice());
        String description = String.format("Advert id: %d, title: %s has been bought.", advertId, advert.getTitle());
        historyRecord(Operation.DECREASE, advert.getPrice(), buyerWallet, description);
        historyRecord(Operation.INCREASE, advert.getPrice(), advertOwnerWallet, description);
        return "Successful transaction";
    }

    @Override
    @LogExecution
    @Transactional
    public OutputAdvertDto buyService(Principal principal, Long serviceId, Long advertId) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        Advert advert = advertService.getAdvertById(advertId);
        if (!user.getId().equals(advert.getUser().getId())) {
            throw new ExpectationFailedException("Advert does not belong to this user");
        }
        if (Boolean.TRUE.equals(advert.getBan())) {
            throw new ConflictException("This advert is banned");
        }
        if (Boolean.TRUE.equals(advert.getPremium())) {
            throw new ConflictException("This advert already has a premium");
        }
        PremiumService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        if (wallet.getBalance() - service.getPrice() < 0) {
            throw new BadRequestException("Not enough money in the account wallet");
        }
        wallet.setBalance(wallet.getBalance() - service.getPrice());
        advert.setPremiumStart(LocalDate.now());
        advert.setPremiumEnd(LocalDate.now().plusDays(service.getDuration()));
        advert.setPremium(null);
        String description = String.format("Service id: %d, description: %s, has been bought for advert %d.", serviceId, service.getDescription(), advert.getId());
        historyRecord(Operation.DECREASE, service.getPrice(), wallet, description);
        return advertMapper.toDto(advert);
    }

    @Override
    @LogExecution
    @Transactional
    public WalletDto deposit(Double amount, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        wallet.setBalance(wallet.getBalance() + amount);
        String description = String.format("User id: %d add %.2f to wallet, total: %.2f ", user.getId(), amount, wallet.getBalance());
        historyRecord(Operation.INCREASE, amount, wallet, description);
        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Override
    @LogExecution
    public List<TransactionDto> showHistory(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        return wallet.getTransactionsHistory().stream().map(transactionMapper::toDto).toList();
    }

    @Override
    @LogExecution
    public List<PremiumService> showServices() {
        return serviceRepository.findAll();
    }

    private Wallet getWalletFromUser(User user) {
        return walletRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Wallet does not exist"));
    }

    private void historyRecord(Operation operation, Double amount, Wallet wallet, String description) {
        TransactionRecord transactionRecord = TransactionRecord.builder()
                .operation(operation)
                .sum(amount)
                .wallet(wallet)
                .description(description)
                .build();
        transactionRepository.save(transactionRecord);
    }

}
