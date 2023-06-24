package com.pokotilov.finaltask.services.advert;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.InputFindRequest;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.*;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.exceptions.UnprocessableEntityException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.mapper.CommentMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.VoteRepository;
import com.pokotilov.finaltask.services.user.UserService;
import com.pokotilov.finaltask.util.Spec;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private AdvertRepository advertRepository;
    @Mock
    private AdvertMapper advertMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private VoteRepository voteRepository;
    @InjectMocks
    private AdvertServiceImpl advertService;

    Long advertId = 1L;
    Principal principal = new UserPrincipal("test@example.com");



    @Test
    void findAdverts_returnPage() {
        // Arrange
        InputFindRequest request = new InputFindRequest();
        request.setTitle("Test Advert");
        request.setPriceMin(10.0);
        request.setPriceMax(100.0);
        request.setRating(4.0f);
        request.setSortField("title");
        request.setSortDirection("ASC");
        request.setPageNo(1);
        request.setPageSize(10);

        List<Advert> adverts = new ArrayList<>();
        adverts.add(Advert.builder().id(1L).title("Test Advert 1").price(50.0).build());
        adverts.add(Advert.builder().id(1L).title("Test Advert 2").price(20.0).build());
        adverts.add(Advert.builder().id(1L).title("Test Advert 3").price(80.0).build());

        Page<Advert> page = new PageImpl<>(adverts);

        when(advertRepository.findAll(any(Spec.class), any(Pageable.class))).thenReturn(page);
        when(advertMapper.toDto(any(Advert.class))).thenReturn(new OutputAdvertDto());

        // Act
        Page<OutputAdvertDto> result = advertService.findAdverts(request);

        // Assert
        verify(advertRepository).findAll(any(Spec.class), any(Pageable.class));
        verify(advertMapper, times(adverts.size())).toDto(any(Advert.class));

        assertEquals(adverts.size(), result.getContent().size());
    }

    @Test
    void getAdvert_shouldGetExistingAdvert() {
        // Arrange

        when(advertRepository.findById(advertId))
                .thenAnswer(invocation -> Optional.of(Advert.builder().id(invocation.getArgument(0)).build()));
        when(advertMapper.toDto(any(Advert.class)))
                .thenAnswer(invocation -> {
                    Advert advert = invocation.getArgument(0);
                    return OutputAdvertDto.builder()
                            .id(advert.getId())
                            .build();
                });

        // Act
        OutputAdvertDto output = advertService.getAdvert(advertId);

        // Assert
        assertEquals(advertId, output.getId());
    }

    @Test
    void createAdvert_returnCreatedAdvert() {
        // Arrange
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();

        when(userService.getUserByPrincipal(principal))
                .thenAnswer(invocation -> User.builder().id(1L).build());
        when(advertRepository.save(any(Advert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(advertMapper.toDto(any(Advert.class)))
                .thenAnswer(invocation -> {
                    Advert advert = invocation.getArgument(0);
                    return OutputAdvertDto.builder()
                            .title(advert.getTitle())
                            .description(advert.getDescription())
                            .price(advert.getPrice())
                            .userId(advert.getUser().getId())
                            .build();
                });

        // Act
        OutputAdvertDto result = advertService.createAdvert(inputAdvertDto, principal);
        // Assert
        assertNotNull(result);
        assertEquals("Test Advert", result.getTitle());
        assertEquals("Test Advert Description", result.getDescription());
        assertEquals(1000.0, result.getPrice());
        assertEquals(1L, result.getUserId());

    }

    @Test
    void updateAdvert_userAuthor_returnUpdated() {
        // Arrange
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();
        User user = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(user)
                .build();
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        when(advertRepository.save(any(Advert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);

        doAnswer(invocation -> {
            InputAdvertDto mapperDto = invocation.getArgument(0);
            Advert mapperAdvert = invocation.getArgument(1);
            mapperAdvert.setTitle(mapperDto.getTitle());
            mapperAdvert.setPrice(mapperDto.getPrice());
            mapperAdvert.setDescription(mapperDto.getDescription());
            return null;
        }).when(advertMapper).updateAdvert(inputAdvertDto, advert);

        when(advertMapper.toDto(any(Advert.class)))
                .thenAnswer(invocation -> {
                    Advert advertInput = invocation.getArgument(0);
                    return OutputAdvertDto.builder()
                            .id(advertInput.getId())
                            .title(advertInput.getTitle())
                            .description(advertInput.getDescription())
                            .price(advertInput.getPrice())
                            .build();
                });

        OutputAdvertDto expected = OutputAdvertDto.builder()
                .id(advert.getId())
                .title(inputAdvertDto.getTitle())
                .description(inputAdvertDto.getDescription())
                .price(inputAdvertDto.getPrice())
                .build();
        // Act
        OutputAdvertDto result = advertService.updateAdvert(advertId, inputAdvertDto, principal);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void updateAdvert_byAdmin_returnUpdated() {
        // Arrange
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();
        User user = User.builder()
                .id(1L)
                .role(Role.ADMIN)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(2L).build())
                .build();

        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        when(advertRepository.save(any(Advert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);

        doAnswer(invocation -> {
            InputAdvertDto mapperDto = invocation.getArgument(0);
            Advert mapperAdvert = invocation.getArgument(1);
            mapperAdvert.setTitle(mapperDto.getTitle());
            mapperAdvert.setPrice(mapperDto.getPrice());
            mapperAdvert.setDescription(mapperDto.getDescription());
            return null;
        }).when(advertMapper).updateAdvert(inputAdvertDto, advert);

        when(advertMapper.toDto(any(Advert.class)))
                .thenAnswer(invocation -> {
                    Advert advertInput = invocation.getArgument(0);
                    return OutputAdvertDto.builder()
                            .id(advertInput.getId())
                            .title(advertInput.getTitle())
                            .description(advertInput.getDescription())
                            .price(advertInput.getPrice())
                            .build();
                });

        OutputAdvertDto expected = OutputAdvertDto.builder()
                .id(advert.getId())
                .title(inputAdvertDto.getTitle())
                .description(inputAdvertDto.getDescription())
                .price(inputAdvertDto.getPrice())
                .build();
        // Act
        OutputAdvertDto result = advertService.updateAdvert(advertId, inputAdvertDto, principal);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void updateAdvert_byNotAuthor_shouldThrow() {
        // Arrange
        Long advertId = 1L;
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        User user = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(2L).build())
                .build();
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));

        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);
        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> advertService.updateAdvert(advertId, inputAdvertDto, principal));
    }

    @Test
    void deleteAdvert_byUserAuthor_shouldDelete() {
        // Arrange
        Long advertId = 1L;
        Principal principal = new UserPrincipal("test@example.com");
        User user = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(user)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        // Act
        String result = advertService.deleteAdvert(advertId, principal);
        // Assert
        assertEquals("Successful deleted", result);
        verify(advertRepository).deleteById(advertId);
    }

    @Test
    void deleteAdvert_byAdmin_shouldDelete() {
        // Arrange
        User user = User.builder()
                .id(2L)
                .role(Role.ADMIN)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(user)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        // Act
        String result = advertService.deleteAdvert(advertId, principal);
        // Assert
        assertEquals("Successful deleted", result);
        verify(advertRepository).deleteById(advertId);
    }

    @Test
    void deleteAdvert_byUserNotAuthor_shouldThrow() {
        // Arrange
        User user = User.builder()
                .id(2L)
                .role(Role.USER)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(advertId).build())
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> advertService.deleteAdvert(advertId, principal));
    }

    @Test
    void banAdvert_returnBannedDto() {
        // Arrange
        Long advertId = 1L;
        Advert advert = Advert.builder()
                .id(advertId)
                .ban(false)
                .build();
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        when(advertRepository.save(any(Advert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(advertMapper.toDto(any(Advert.class)))
                .thenAnswer(invocation -> {
                    Advert advertInput = invocation.getArgument(0);
                    return OutputAdvertDto.builder()
                            .id(advertInput.getId())
                            .ban(advertInput.getBan())
                            .build();
                });
        // Act
        OutputAdvertDto result = advertService.banAdvert(advertId);
        // Assert
        assertTrue(result.getBan());
    }

    @Test
    void voteAdvert_byNotAdvertAuthor_return() {
        // Arrange
        VoteDto voteDto = VoteDto.builder()
                .vote(3)
                .advertId(advertId)
                .build();
        User user = User.builder()
                .id(2L)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(1L).build())
                .build();
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);
        when(voteRepository.save(any(Vote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        String result = advertService.voteAdvert(voteDto, principal);
        // Assert
        assertEquals("Successful vote", result);
    }

    @Test
    void voteAdvert_byAdvertAuthor_throw() {
        // Arrange
        VoteDto voteDto = VoteDto.builder()
                .vote(3)
                .advertId(advertId)
                .build();
        User user = User.builder()
                .id(2L)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(user)
                .build();
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        when(userService.getUserByPrincipal(principal))
                .thenReturn(user);
        // Act
        assertThrows(UnprocessableEntityException.class, () -> advertService.voteAdvert(voteDto, principal));
    }

    @Test
    void getAdvertComments_validAdvertId_returnCommentList() {
        // Arrange
        Comment first = Comment.builder()
                .id(3L)
                .ban(false)
                .build();
        Comment second = Comment.builder()
                .id(2L)
                .ban(true)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .comments(List.of(first, second))
                .build();
        OutputCommentDto expected = OutputCommentDto.builder()
                .id(3L)
                .ban(false)
                .build();
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(advert));
        when(commentMapper.toDto(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    return OutputCommentDto.builder()
                            .id(comment.getId())
                            .ban(comment.getBan())
                            .build();
                });
        // Act
        List<OutputCommentDto> output = advertService.getAdvertComments(advertId);
        // Assert
        assertEquals(1, output.size());
        assertEquals(expected, output.get(0));

    }

    @Test
    void getAdvertById_validAdvertId_returnAdvert() {
        // Arrange
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.of(Advert.builder()
                        .id(advertId)
                        .build())
                );
        // Act
        Advert result = advertService.getAdvertById(advertId);
        // Assert
        assertEquals(advertId, result.getId());
    }

    @Test
    void getAdvertById_invalidAdvertId_shouldThrow() {
        // Arrange
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> advertService.getAdvertById(advertId));
    }
}