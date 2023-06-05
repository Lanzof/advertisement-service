package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;

import java.security.Principal;

public interface ICommentService {
    String createComment(InputCommentDto inputCommentDto, Principal principal);

    String banComment(Long commentId);
}
