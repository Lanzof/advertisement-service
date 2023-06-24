package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Методы для работы с комментариями.")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final AdvertService advertService;

    @PostMapping("/{advertId}/comments")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public OutputCommentDto createComment(@Valid @RequestBody InputCommentDto comment, Principal principal) {
        return commentService.createComment(comment, principal);
    }

    @GetMapping("/{advertId}/comments")
    public List<OutputCommentDto> getAdvertComments(@PathVariable("advertId") @Positive Long id) {
        return advertService.getAdvertComments(id);
    }

    @PutMapping("/{commentId}/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    public OutputCommentDto banComment(@PathVariable("commentId") Long id) {
        return commentService.banComment(id);
    }
}
