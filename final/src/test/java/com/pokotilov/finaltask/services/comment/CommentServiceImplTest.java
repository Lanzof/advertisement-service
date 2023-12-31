package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.mapper.CommentMapper;
import com.pokotilov.finaltask.repositories.CommentRepository;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.user.UserService;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private AdvertService advertService;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void createComment_validParams_returnSavedComment() {
        // arrange
        Long advertId = 1L;
        String text = "Test comment";
        Principal principal = new UserPrincipal("test@example.com");
        User user = User.builder()
                .id(2L)
                .build();
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(1L).build())
                .build();
        InputCommentDto inputCommentDto = InputCommentDto.builder()
                .advertId(advertId)
                .text(text)
                .build();
        when(userService.getUserByPrincipal(principal)).thenReturn(user);
        when(advertService.getAdvertById(advertId)).thenReturn(advert);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(commentMapper.toDto(any(Comment.class))).thenAnswer(invocation -> {
            Comment com = invocation.getArgument(0);
            return OutputCommentDto.builder()
                    .text(com.getText())
                    .ban(com.getBan())
                    .build();
        });

        // act
        OutputCommentDto result = commentService.createComment(inputCommentDto, principal);

        // assert
        assertEquals(inputCommentDto.getText(), result.getText());
    }

    @Test
    void banComment_nonExistingComment_shouldThrow() {
        // arrange
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> commentService.banComment(commentId));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void banComment_ExistingComment_returnBannedComment() {
        // arrange
        Long commentId = 1L;
        Comment comment = Comment.builder()
                .id(commentId)
                .ban(false)
                .build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenAnswer(invocation -> {
            Comment com = invocation.getArgument(0);
            return OutputCommentDto.builder()
                    .ban(com.getBan())
                    .build();
        });

        // act
        OutputCommentDto result = commentService.banComment(commentId);

        // assert
        assertTrue(result.getBan());
        verify(commentRepository, times(1)).save(comment);
    }
}