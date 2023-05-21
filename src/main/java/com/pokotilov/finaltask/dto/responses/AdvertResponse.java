package com.pokotilov.finaltask.dto.responses;

import com.pokotilov.finaltask.dto.AdvertDto;
import com.pokotilov.finaltask.dto.CommentDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertResponse {
    private List<AdvertDto> advertDtos;
    private List<Advert> adverts;
    private String message;
    private List<CommentDto> commentDtos;
    private Set<Comment> comment;
}
