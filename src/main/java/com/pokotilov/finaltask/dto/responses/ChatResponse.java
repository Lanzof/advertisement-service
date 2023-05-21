package com.pokotilov.finaltask.dto.responses;

import com.pokotilov.finaltask.entities.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private List<Chat> chats;
    private String message;
}
