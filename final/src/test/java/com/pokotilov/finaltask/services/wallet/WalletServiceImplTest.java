package com.pokotilov.finaltask.services.wallet;

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
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private AdvertMapper advertMapper;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    WalletServiceImpl walletService;

    @Test
    void createWallet_walletDoesntExist_returnWallet() {
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
        when(walletMapper.toDto(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);
                    return WalletDto.builder()
                            .balance(wallet.getBalance())
                            .userId(wallet.getUser().getId())
                            .build();
                });

        // Act
        WalletDto output = walletService.createWallet(principal);

        // Assert
        assertEquals(0.00, output.getBalance());
        assertEquals(1L, output.getUserId());
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
    void getWallet_walletExist_returnWalletDto() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .wallet(Wallet.builder().balance(1500.00).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        when(walletMapper.toDto(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);
                    return WalletDto.builder()
                            .balance(wallet.getBalance())
                            .build();
                });

        // Act
        WalletDto output = walletService.getWallet(principal);

        // Assert
        assertEquals(1500.00, output.getBalance());
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
        assertThrows(NotFoundException.class, () -> walletService.getWallet(principal), "Wallet does not exist");
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
                .description(String.format("Advert id: %d, title: %s has been bought.", advertId, advert.getTitle()))
                .operation(Operation.DECREASE)
                .sum(advert.getPrice())
                .wallet(principalUser.getWallet())
                .build();

        // Act
        String output = walletService.buyAdvert(principal, advertId);

        // Assert
        verify(transactionRepository).save(savedTransaction.capture());
        assertEquals("Successful transaction", output);
        assertEquals(expectedTransaction.getDescription(), savedTransaction.getValue().getDescription());
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
    void buyService_validAdvertAndWalletBalance_returnUpdatedAdvertDto() {
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
                .description(String.format("Service id: %d, description: %s, has been bought for advert %d.", serviceId, service.getDescription(), advert.getId()))
                .operation(Operation.DECREASE)
                .sum(service.getPrice())
                .wallet(principalUser.getWallet())
                .build();
        when(advertMapper.toDto(any(Advert.class))).thenAnswer(invocation -> {
            Advert ad = invocation.getArgument(0);
            return OutputAdvertDto.builder()
                    .premium(ad.getPremium())
                    .build();
        });

        // Act
        OutputAdvertDto output = walletService.buyService(principal, serviceId, advertId);

        // Assert
        verify(transactionRepository).save(savedTransaction.capture());
        assertTrue(output.getPremium());
        assertEquals(expectedTransaction.getDescription(), savedTransaction.getValue().getDescription());
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
                .description(String.format("User id: %d add %f to wallet, total: %f ", principalUser.getId(), amount, 2000.00))
                .operation(Operation.INCREASE)
                .sum(amount)
                .wallet(principalUser.getWallet())
                .build();
        when(walletMapper.toDto(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);
                    return WalletDto.builder()
                            .balance(wallet.getBalance())
                            .build();
                });

        // Act
        WalletDto output = walletService.deposit(amount, principal);

        // Assert
        verify(transactionRepository).save(savedTransaction.capture());
        assertEquals(expectedTransaction.getDescription(), savedTransaction.getValue().getDescription());
        assertEquals(2000.00, output.getBalance());
    }

    @Test
    void showHistory_authUser_returnTransactionalHistoryDto() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");

        List<Transaction> list = List.of(
                Transaction.builder().id(1L).description("text1").build(),
                Transaction.builder().id(2L).description("text2").build(),
                Transaction.builder().id(3L).description("text3").build()
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
        when(transactionMapper.toDto(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tr = invocation.getArgument(0);
            return TransactionDto.builder()
                    .description(tr.getDescription())
                    .build();
        });

        // Act
        List<TransactionDto> output = walletService.showHistory(principal);

        // Assert
        assertEquals(list.get(0).getDescription(), output.get(0).getDescription());
    }
}