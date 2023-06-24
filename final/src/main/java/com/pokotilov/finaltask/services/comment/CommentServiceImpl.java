package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.aop.LogExecution;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@LogExecution
public class CommentServiceImpl implements CommentService {

    private final AdvertService advertService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public OutputCommentDto createComment(InputCommentDto inputCommentDto, Principal principal) {
        Advert advert = advertService.getAdvertById(inputCommentDto.getAdvertId());
        User user = userService.getUserByPrincipal(principal);
        Comment comment = Comment.builder()
                .advert(advert)
                .author(user)
                .text(inputCommentDto.getText())
                .ban(false)
                .build();
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public OutputCommentDto banComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setBan(true);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
