package com.pokotilov.finaltask.services.wallet;

import com.pokotilov.finaltask.dto.WalletDto;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.BadRequestException;
import com.pokotilov.finaltask.exceptions.ConflictException;
import com.pokotilov.finaltask.exceptions.ExpectationFailedException;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.repositories.ServiceRepository;
import com.pokotilov.finaltask.repositories.TransactionRepository;
import com.pokotilov.finaltask.repositories.WalletRepository;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.user.UserService;
import com.sun.security.auth.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private AdvertService advertService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @InjectMocks
    WalletServiceImpl walletService;

    @Test
    void createWallet_walletDoesntExist_returnSuccess() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);
                    wallet.setId(2L);
                    return wallet;
                });
        ArgumentCaptor<Wallet> savedWallet = ArgumentCaptor.forClass(Wallet.class);

        // Act
        WalletDto output = walletService.createWallet(principal);

        // Assert
        verify(walletRepository).save(savedWallet.capture());
        assertEquals(2L, savedWallet.getValue().getId());
        assertEquals("Wallet created", output);
    }

    @Test
    void createWallet_walletExist_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(new Wallet())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);

        // Act & Assert
        assertThrows(ConflictException.class, () -> walletService.createWallet(principal), "Wallet already exist");

        verify(walletRepository, times(0)).save(any());
    }

    @Test
    void getWallet_walletExist_returnBalance() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);

        // Act
        String output = walletService.getWallet(principal);

        // Assert
        assertEquals(Double.toString(1500.00), output);
    }

    @Test
    void getWallet_walletDoesntExist_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(null)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> walletService.getWallet(principal), "Wallet does not exist");
    }

    @Test
    void buyAdvert_validAdvertAndWalletBalance_returnSuccess() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(User.builder().id(2L).build())
                .price(1000.00)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        ArgumentCaptor<Transaction> savedTransaction = ArgumentCaptor.forClass(Transaction.class);
        Transaction expectedTransaction = Transaction.builder()
                .description(String.format("Advert id: %d, title: %s", advertId, advert.getTitle()))
                .operation(Operation.DECREASE)
                .sum(advert.getPrice())
                .wallet(principalUser.getWallet())
                .build();

        // Act
        String output = walletService.buyAdvert(principal, advertId);

        // Assert
        verify(transactionRepository).save(savedTransaction.capture());
        assertEquals("Successful transaction", output);
        assertEquals(expectedTransaction, savedTransaction.getValue());
    }

    @Test
    void buyAdvert_notEnoughWalletBalance_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(User.builder().id(2L).build())
                .price(2000.00)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> walletService.buyAdvert(principal, advertId), "Not enough money in the account wallet");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void buyAdvert_advertBelongToPrincipal_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(principalUser)
                .price(1000.00)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);

        // Act & Assert
        assertThrows(ExpectationFailedException.class, () -> walletService.buyAdvert(principal, advertId), "You cannot buy your own ad");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void buyService_validAdvertAndWalletBalance_returnSuccess() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(principalUser)
                .price(1000.00)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        Long serviceId = 1L;
        PremiumService service = PremiumService.builder()
                .id(serviceId)
                .price(500.00)
                .duration(30)
                .build();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        ArgumentCaptor<Transaction> savedTransaction = ArgumentCaptor.forClass(Transaction.class);
        Transaction expectedTransaction = Transaction.builder()
                .description(String.format("Service id: %d, description: %s", serviceId, service.getDescription()))
                .operation(Operation.DECREASE)
                .sum(service.getPrice())
                .wallet(principalUser.getWallet())
                .build();

        // Act
        String output = walletService.buyService(principal, serviceId, advertId);

        // Assert
        verify(transactionRepository).save(savedTransaction.capture());
        assertEquals("Successful transaction", output);
        assertEquals(expectedTransaction, savedTransaction.getValue());
        assertEquals(LocalDate.now(), advert.getPremiumStart());
        assertEquals(LocalDate.now().plusDays(service.getDuration()), advert.getPremiumEnd());
        assertEquals(1000.00, principalUser.getWallet().getBalance());
    }

    @Test
    void buyService_notEnoughBalance_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(200.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(principalUser)
                .price(1000.00)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        Long serviceId = 1L;
        PremiumService service = PremiumService.builder()
                .id(serviceId)
                .price(500.00)
                .duration(30)
                .build();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> walletService.buyService(principal, serviceId, advertId), "Not enough money in the account wallet");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void buyService_serviceDoesntExist_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(principalUser)
                .price(1000.00)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        Long serviceId = 1L;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> walletService.buyService(principal, serviceId, advertId), "Service not found");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void buyService_advertIsBanned_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(principalUser)
                .price(1000.00)
                .ban(true)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        Long serviceId = 1L;

        // Act & Assert
        assertThrows(ConflictException.class, () -> walletService.buyService(principal, serviceId, advertId), "This advert is banned");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void buyService_advertHasPremium_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(principalUser)
                .price(1000.00)
                .ban(false)
                .premiumEnd(LocalDate.now().plusDays(1))
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        Long serviceId = 1L;

        // Act & Assert
        assertThrows(ConflictException.class, () -> walletService.buyService(principal, serviceId, advertId), "This advert already has a premium premium");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void buyService_advertDoesntBelongToPrincipal_shouldThrow() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 2L;
        Advert advert = Advert.builder()
                .id(advertId)
                .title("Title")
                .user(User.builder().id(2L).build())
                .price(1000.00)
                .ban(true)
                .build();
        when(advertService.getAdvertById(advertId))
                .thenReturn(advert);
        Long serviceId = 1L;

        // Act & Assert
        assertThrows(ExpectationFailedException.class, () -> walletService.buyService(principal, serviceId, advertId), "Advert does not belong to this user");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void deposit_validWallet_returnUpdatedWallet() {
        // Arrange
        Double amount = 500.00;
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Transaction> savedTransaction = ArgumentCaptor.forClass(Transaction.class);
        Transaction expectedTransaction = Transaction.builder()
                .description(String.format("User id: %d add %f to wallet, total: %f ", principalUser.getId(), amount, principalUser.getWallet().getBalance()))
                .operation(Operation.INCREASE)
                .sum(amount)
                .wallet(principalUser.getWallet())
                .build();

        // Act
        Wallet output = walletService.deposit(amount, principal);

        // Assert
        verify(transactionRepository).save(savedTransaction.capture());
        assertEquals(2000.00, output.getBalance());
    }

    @Test
    void showHistory() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");

        List<Transaction> list = List.of(
                Transaction.builder().id(1L).build(),
                Transaction.builder().id(2L).build(),
                Transaction.builder().id(3L).build()
        );
        Wallet wallet = Wallet.builder()
                .balance(1500.00)
                .transactionsHistory(list)
                .build();
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(wallet)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);

        // Act
        List<Transaction> output = walletService.showHistory(principal);

        // Assert
        assertEquals(list, output);
    }
}