package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.services.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Методы для работы с комментариями.")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> createComment(@Valid @RequestBody InputCommentDto comment, Principal principal) {
        return ResponseEntity.ok().body(commentService.createComment(comment, principal));
    }

    @PutMapping("/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> banComment(@RequestBody Long id) {
        return ResponseEntity.ok().body(commentService.banComment(id));
    }
}
