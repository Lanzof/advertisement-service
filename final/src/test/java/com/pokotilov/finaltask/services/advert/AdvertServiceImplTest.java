package com.pokotilov.finaltask.services.advert;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.Role;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.mapper.CommentMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.services.user.UserService;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
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

    @InjectMocks
    private AdvertServiceImpl advertService;



    @Test
    void findAdverts() {
        // Arrange

    }

    @Test
    void getAdvert_shouldGetExistingAdvert() {
        // Arrange
        Long advertId = 1L;
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
        //a
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
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

        //a
        OutputAdvertDto result = advertService.createAdvert(inputAdvertDto, principal);
        //a
        assertNotNull(result);
        assertEquals("Test Advert", result.getTitle());
        assertEquals("Test Advert Description", result.getDescription());
        assertEquals(1000.0, result.getPrice());
        assertEquals(1L, result.getUserId());

    }

    @Test
    void updateAdvert_userAuthor_returnUpdated() {
        //a
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
        //a
        OutputAdvertDto result = advertService.updateAdvert(advertId, inputAdvertDto, principal);
        //a
        assertEquals(expected, result);
    }

    @Test
    void updateAdvert_byAdmin_returnUpdated() {
        //a
        Long advertId = 1L;
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
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
        //a
        OutputAdvertDto result = advertService.updateAdvert(advertId, inputAdvertDto, principal);
        //a
        assertEquals(expected, result);
    }

    @Test
    void updateAdvert_byNotAuthor_shouldThrow() {
        //a
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
        //a
        assertThrows(AccessDeniedException.class, () -> advertService.updateAdvert(advertId, inputAdvertDto, principal));
    }

    @Test
    void deleteAdvert_byUserAuthor_shouldDelete() {
        //a
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
        //a
        String result = advertService.deleteAdvert(advertId, principal);
        //a
        assertEquals("Successful deleted", result);
    }

    @Test
    void deleteAdvert_byAdmin_shouldDelete() {
        //a
        Long advertId = 1L;
        Principal principal = new UserPrincipal("test@example.com");
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
        //a
        String result = advertService.deleteAdvert(advertId, principal);
        //a
        assertEquals("Successful deleted", result);
    }

    @Test
    void deleteAdvert_byUserNotAuthor_shouldThrow() {
        //a
        Long advertId = 1L;
        Principal principal = new UserPrincipal("test@example.com");
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
        //a
        assertThrows(AccessDeniedException.class, () -> advertService.deleteAdvert(advertId, principal));
    }

    @Test
    void banAdvert_returnBannedDto() {
        //a
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
        //a
        OutputAdvertDto result = advertService.banAdvert(advertId);
        //a
        assertTrue(result.getBan());
    }

    @Test
    void getAdvertComments_validAdvertId_returnCommentList() {
        //a
        Comment first = Comment.builder()
                .id(3L)
                .ban(false)
                .build();
        Comment second = Comment.builder()
                .id(2L)
                .ban(true)
                .build();
        Long advertId = 1L;
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
        //a
        List<OutputCommentDto> output = advertService.getAdvertComments(advertId);
        //a
        assertEquals(1, output.size());
        assertEquals(expected, output.get(0));

    }

    @Test
    void getAdvertById_validAdvertId_returnAdvert() {
        Long advertId = 1L;
        when(advertRepository.findById(advertId))
                .thenAnswer(invocation -> Optional.of(Advert.builder().id(invocation.getArgument(0)).build()));

        Advert result = advertService.getAdvertById(advertId);

        assertEquals(advertId, result.getId());
    }

    @Test
    void getAdvertById_invalidAdvertId_shouldThrow() {
        Long advertId = 1L;
        when(advertRepository.findById(advertId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> advertService.getAdvertById(advertId));
    }
}