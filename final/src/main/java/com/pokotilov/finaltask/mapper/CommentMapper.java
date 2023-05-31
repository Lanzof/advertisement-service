package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.comments.OutputCommentDto;
import com.pokotilov.finaltask.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "advertId",
            expression = "java(comment.getAdvert().getId())")
    @Mapping(target = "authorId",
            expression = "java(comment.getAuthor().getId())")
    @Mapping(target = "authorFirstName",
            expression = "java(comment.getAuthor().getFirstName())")
    @Mapping(target = "authorLastName",
            expression = "java(comment.getAuthor().getLastName())")
    OutputCommentDto toDto(Comment comment);
}
