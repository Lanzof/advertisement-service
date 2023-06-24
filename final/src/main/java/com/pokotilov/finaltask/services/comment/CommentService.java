package com.pokotilov.finaltask.services.comment;

import com.pokotilov.finaltask.dto.comments.InputCommentDto;
import com.pokotilov.finaltask.dto.comments.OutputCommentDto;

import java.security.Principal;

public interface CommentService {
    OutputCommentDto createComment(InputCommentDto inputCommentDto, Principal principal);

    OutputCommentDto banComment(Long commentId);
}
