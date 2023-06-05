package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
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
public class CommentService implements ICommentService {

    private final AdvertRepository advertRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Override
    public String createComment(InputCommentDto inputCommentDto, Principal principal) {
        Advert advert = advertRepository.findById(inputCommentDto.getAdvertId()).orElseThrow(() -> new NotFoundException("This advert doesn't exist."));
        User user =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
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
        Comment comment = commentRepository.getReferenceById(commentId);
        comment.setBan(true);
        commentRepository.save(comment);
        return "Successful block";
    }
}
