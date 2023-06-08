package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;

import java.security.Principal;

public interface CommentService {
    String createComment(InputCommentDto inputCommentDto, Principal principal);

    String banComment(Long commentId);
}
