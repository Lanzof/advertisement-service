package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.ConflictException;
import com.pokotilov.finaltask.exceptions.ExpectationFailedException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AdvertRepository advertRepository;
    private final TransactionRepository transactionRepository;
    private final ServiceRepository serviceRepository;


    @Transactional
    public String createWallet(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getWallet() != null) {
            throw new ConflictException("Wallet already exist");
        }
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);
        return "Wallet created";
    }

    public String getWallet(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new EntityNotFoundException("Wallet does not exist");
        }
        return wallet.getBalance().toString();
    }

    @Transactional
    public String buyAdvert(Principal principal, Long advertId) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        Advert advert = advertRepository.getReferenceById(advertId);
        if (user.getId().equals(advert.getUser().getId())) {
            throw new ExpectationFailedException("You cannot buy your own ad");
        }
        if (wallet.getBalance() - advert.getPrice() < 0) {
            throw new BadRequestException("Not enough money in the account wallet");
        }
        wallet.setBalance(wallet.getBalance() - advert.getPrice());
        Transaction transaction = new Transaction();
        transaction.setOperation(Operation.DECREASE);
        transaction.setSum(advert.getPrice());
        transaction.setWallet(wallet);
        transaction.setDescription(String.format("Advert id: %d, title: %s", advertId, advert.getTitle()));
        transactionRepository.save(transaction);
        return "Successful transaction";
    }

    @Transactional
    public String buyService(Principal principal, Long serviceId, Long advertId) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        Advert advert = advertRepository.getReferenceById(advertId);
        if (!user.getId().equals(advert.getUser().getId())) {
            throw new ExpectationFailedException("Advert does not belong to this user");
        }
        if (advert.getPremium()) {
            throw new ConflictException("This advert already has a premium premium");
        }
        if (advert.getBan()) {
            throw new ConflictException("This advert is banned");
        }
        PremiumService service = serviceRepository.getReferenceById(serviceId);
        if (wallet.getBalance() - service.getPrice() < 0) {
            throw new BadRequestException("Not enough money in the account wallet");
        }
        wallet.setBalance(wallet.getBalance() - service.getPrice());
        advert.setPremiumStart(LocalDate.now());
        advert.setPremiumEnd(LocalDate.now().plusDays(service.getDuration()));
        Transaction transaction = new Transaction();
        transaction.setOperation(Operation.DECREASE);
        transaction.setSum(service.getPrice());
        transaction.setWallet(wallet);
        transaction.setDescription(String.format("Service id: %d, description: %s", serviceId, service.getDescription()));
        transactionRepository.save(transaction);
        return "Successful transaction";
    }

    @Transactional
    public String deposit(Double amount, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new EntityNotFoundException("Wallet does not exist");
        }
        wallet.setBalance(wallet.getBalance() + amount);
        Transaction transaction = new Transaction();
        transaction.setOperation(Operation.INCREASE);
        transaction.setSum(amount);
        transaction.setWallet(wallet);
        transaction.setDescription(String.format("User id: %d add %f to wallet, total: %f ", user.getId(), amount, wallet.getBalance()));
        transactionRepository.save(transaction);
        return String.format("User id: %d add %f to wallet, total: %f ", user.getId(), amount, wallet.getBalance());
    }


    @Transactional
    public List<Transaction> showHistory(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new EntityNotFoundException("Wallet does not exist");
        }
        return wallet.getTransactionsHistory();
    }

    public List<PremiumService> showServices() {
        return serviceRepository.findAll();
    }
}
