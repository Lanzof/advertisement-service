package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.dto.responses.ChatResponse;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Chat;
import com.pokotilov.finaltask.entities.Message;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.ChatRepository;
import com.pokotilov.finaltask.repositories.MessageRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final AdvertRepository advertRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChatResponse createChat(Long advertId, Principal principal) {
        Advert advert = advertRepository.getReferenceById(advertId);
        User user =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Chat chat = Chat.builder()
                .advert(advert)
                .buyer(user)
                .build();
        chatRepository.save(chat);
        return ChatResponse.builder()
                .chats(List.of(chat))
                .build();
    }

    public ChatResponse sendMessage(MessageDto messageDto) {
        Chat chat = chatRepository.getReferenceById(messageDto.getChatId());
        Message message = Message.builder()
                .chat(chat)
                .date(LocalDateTime.now())
                .text(messageDto.getText())
                .build();
        messageRepository.save(message);//todo here must be a real chat
        return ChatResponse.builder()
                .message("Successful send")
                .build();
    }
}
