package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.CommentRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AdvertRepository advertRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    public String createComment(@Valid @RequestBody InputCommentDto inputCommentDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(inputCommentDto.getAdvertId());
        User user =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Comment comment = Comment.builder()
                .advert(advert)
                .author(user)
                .text(inputCommentDto.getText())
                .ban(false)
                .build();
        commentRepository.save(comment);
        return "Successful add";
    }

    public String banComment(Long commentId) {
        Comment comment = commentRepository.getReferenceById(commentId);
        comment.setBan(true);
        commentRepository.save(comment);
        return "Successful block";
    }
}
