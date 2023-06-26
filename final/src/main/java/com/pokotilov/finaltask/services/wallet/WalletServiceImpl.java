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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@LogExecution
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
    @Transactional
    public WalletDto createWallet(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        if (user.getWallet() != null) {
            throw new ConflictException("Wallet already exist");
        }
        Wallet wallet = new Wallet();
        wallet.setBalance(0.00);
        wallet.setUser(user);

        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Override
    public WalletDto getWallet(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        return walletMapper.toDto(wallet);
    }

    @Override
    @Transactional
    public String buyAdvert(Principal principal, Long advertId) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        Advert advert = advertService.getAdvertById(advertId);
        if (user.getId().equals(advert.getUser().getId())) {
            throw new ExpectationFailedException("You cannot buy your own ad");
        }
        if (wallet.getBalance() - advert.getPrice() < 0) {
            throw new BadRequestException("Not enough money in the account wallet");
        }
        wallet.setBalance(wallet.getBalance() - advert.getPrice());
        String description = String.format("Advert id: %d, title: %s has been bought.", advertId, advert.getTitle());
        Transaction transaction = Transaction.builder()
                .operation(Operation.DECREASE)
                .sum(advert.getPrice())
                .wallet(wallet)
                .description(description)
                .build();
        transactionRepository.save(transaction);
        return "Successful transaction";
    }

    @Override
    @Transactional
    public OutputAdvertDto buyService(Principal principal, Long serviceId, Long advertId) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        Advert advert = advertService.getAdvertById(advertId);
        if (!user.getId().equals(advert.getUser().getId())) {
            throw new ExpectationFailedException("Advert does not belong to this user");
        }
        if (Boolean.TRUE.equals(advert.getPremium())) {
            throw new ConflictException("This advert already has a premium");
        }
        if (Boolean.TRUE.equals(advert.getBan())) {
            throw new ConflictException("This advert is banned");
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
        Transaction transaction = Transaction.builder()
                .operation(Operation.DECREASE)
                .sum(service.getPrice())
                .wallet(wallet)
                .description(description)
                .build();
        transactionRepository.save(transaction);
        return advertMapper.toDto(advert);
    }

    @Override
    @Transactional
    public WalletDto deposit(Double amount, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        wallet.setBalance(wallet.getBalance() + amount);
        String description = String.format("User id: %d add %f to wallet, total: %f ", user.getId(), amount, wallet.getBalance());
        Transaction transaction = Transaction.builder()
                .operation(Operation.INCREASE)
                .sum(amount)
                .wallet(wallet)
                .description(description)
                .build();
        transactionRepository.save(transaction);
        return walletMapper.toDto(walletRepository.save(wallet));
    }


    @Override
    @Transactional
    public List<TransactionDto> showHistory(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Wallet wallet = getWalletFromUser(user);
        return wallet.getTransactionsHistory().stream().map(transactionMapper::toDto).toList();
    }

    @Override
    public List<PremiumService> showServices() {
        return serviceRepository.findAll();
    }

    private static Wallet getWalletFromUser(User user) {
        return Optional.ofNullable(user.getWallet())
                .orElseThrow(() -> new NotFoundException("Wallet does not exist"));
    }
}
