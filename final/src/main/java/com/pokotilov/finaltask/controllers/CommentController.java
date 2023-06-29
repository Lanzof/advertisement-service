package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.comment.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Методы для работы с комментариями.")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final AdvertService advertService;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @PostMapping("/adverts/{advertId}/comments")
    public OutputCommentDto createComment(@Parameter(description = "Id объявления.") @PathVariable("advertId") @Positive Long id,
                                          @RequestParam String text , Principal principal) {
        InputCommentDto comment = new InputCommentDto(id, text);
        Set<ConstraintViolation<InputCommentDto>> violations = validator.validate(comment);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return commentService.createComment(comment, principal);
    }

    @GetMapping("/adverts/{advertId}/comments")
    public List<OutputCommentDto> getAdvertComments(@PathVariable("advertId") @Positive Long id) {
        return advertService.getAdvertComments(id);
    }

    @PutMapping("/comments/{commentId}/ban")
    @Secured({ "ROLE_ADMIN" })
    public OutputCommentDto banComment(@PathVariable("commentId") @Positive Long id) {
        return commentService.banComment(id);
    }
}
