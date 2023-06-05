package com.pokotilov.finaltask.services.advert;

import com.pokotilov.finaltask.dto.advert.InputAdvertDto;
import com.pokotilov.finaltask.dto.advert.OutputAdvertDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.AdvertMapper;
import com.pokotilov.finaltask.mapper.CommentMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertServiceTest {
    @Mock
    private AdvertRepository advertRepository;
    @Mock
    private AdvertMapper advertMapper;
    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private AdvertService advertService;

    @Test
    void findAdverts() {
        // Arrange

    }

    @Test
    void shouldGetExistingAdvert() {
        // Arrange
        Long advertId = 1L;
        OutputAdvertDto exit = OutputAdvertDto.builder()
                .id(1L)
                .build();
        when(advertRepository.findById(anyLong()))
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
        assertEquals(output, exit);
    }

    @Test
    void shouldThrowNotFoundAdvert() {
        // Arrange
        Long advertId = 1L;
        when(advertRepository.findById(anyLong()))
                .thenAnswer(invocation -> Optional.empty());

        // Act
        assertThrows(NotFoundException.class, () -> advertService.getAdvert(advertId));

        // Assert

    }

    @Test
    void createAdvert() {
        //a
        InputAdvertDto inputAdvertDto = InputAdvertDto.builder()
                .title("Test Advert")
                .description("Test Advert Description")
                .price(1000.0)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .then(invocation -> Optional.of(User.builder().id(1L).build()));
        when(advertRepository.save(any(Advert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(advertMapper.toDto(any(Advert.class)))
                .thenAnswer(invocation -> {
                    Advert advert = invocation.getArgument(0);
                    return OutputAdvertDto.builder()
                            .id(advert.getId())
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
    void updateAdvert() {
    }

    @Test
    void deleteAdvert() {
    }

    @Test
    void banAdvert() {
    }

    @Test
    void getAdvertComments() {
        Comment first = Comment.builder()
                .id(1L)
                .text("first text")
                .ban(false)
                .build();
        Comment second = Comment.builder()
                .id(2L)
                .text("second text")
                .ban(true)
                .build();
        Long advertId = 1L;
        Advert advert = Advert.builder()
                .id(1L)
                .comments(List.of(first, second))
                .build();
        OutputCommentDto exit = OutputCommentDto.builder()
                .id(1L)
                .text("first text")
                .ban(false)
                .build();
        when(advertRepository.findById(1L))
                .thenAnswer(invocation -> Optional.of(advert));
        when(commentMapper.toDto(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    return OutputCommentDto.builder()
                            .id(comment.getId())
                            .text(comment.getText())
                            .ban(comment.getBan())
                            .build();
                });

        List<OutputCommentDto> output = advertService.getAdvertComments(advertId);

        assertEquals(output.get(0), exit);

    }
}