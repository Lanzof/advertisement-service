package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.CommentRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final AdvertService advertService;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    public String createComment(InputCommentDto inputCommentDto, Principal principal) {
        Advert advert = advertService.getAdvertById(inputCommentDto.getAdvertId());
        User user = userService.getUserByPrincipal(principal);
        Comment comment = Comment.builder()
                .advert(advert)
                .author(user)
                .text(inputCommentDto.getText())
                .ban(false)
                .build();
        commentRepository.save(comment);
        return "Successful add";
    }

    @Override
    public String banComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setBan(true);
        commentRepository.save(comment);
        return "Successful block";
    }
}
