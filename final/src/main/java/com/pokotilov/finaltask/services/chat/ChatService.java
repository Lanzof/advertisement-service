package com.pokotilov.finaltask.services.chat;

import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.dto.MessageDto;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface ChatService {
    ChatDto getChat(Long advertId, Long userId, Principal principal);

    MessageDto sendMessage(Long chatId, String text, Principal principal);

    Page<ChatDto> getChats(Integer pageNo, Integer pageSize, Principal principal);

    Page<MessageDto> getChatMessages(Integer pageNo, Integer pageSize, Long chatId, Principal principal);
}
