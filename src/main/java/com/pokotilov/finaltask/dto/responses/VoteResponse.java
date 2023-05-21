package com.pokotilov.finaltask.dto.responses;

import com.pokotilov.finaltask.dto.VoteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {
    private List<VoteDto> voteDtos;
    private String message;
}
