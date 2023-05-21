package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.CommentDto;
import com.pokotilov.finaltask.dto.responses.CommentResponse;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.CommentRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AdvertRepository advertRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    public CommentResponse createComment(CommentDto commentDto, Principal principal) {
        Advert advert = advertRepository.getReferenceById(commentDto.getAdvertId());
        User user =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Comment comment = Comment.builder()
                .advert(advert)
                .author(user)
                .date(LocalDateTime.now())
                .text(commentDto.getText())
                .build();
        commentRepository.save(comment);
        return CommentResponse.builder()
                .message("Successful add")
                .build();
    }

    public CommentResponse banComment(Long commentid) {
        Comment comment = commentRepository.getReferenceById(commentid);
        comment.setBan(true);
        commentRepository.save(comment);
        return CommentResponse.builder()
                .message("Successful block")
                .build();
    }
}
