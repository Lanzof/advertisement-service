package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.CommentDto;
import com.pokotilov.finaltask.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<?> createComment(@RequestBody CommentDto comment, Principal principal) {
        return ResponseEntity.ok(commentService.createComment(comment, principal).getMessage());
    }

    @PutMapping("/ban")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> banComment(@RequestBody Long id) {
        return ResponseEntity.ok(commentService.banComment(id).getMessage());
    }
}
