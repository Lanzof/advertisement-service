package com.pokotilov.finaltask.services;

import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Chat;
import com.pokotilov.finaltask.entities.Message;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.ChatException;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import com.pokotilov.finaltask.mapper.ChatMapper;
import com.pokotilov.finaltask.mapper.MessageMapper;
import com.pokotilov.finaltask.repositories.AdvertRepository;
import com.pokotilov.finaltask.repositories.ChatRepository;
import com.pokotilov.finaltask.repositories.MessageRepository;
import com.pokotilov.finaltask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ChatService {

    public static final String USER_NOT_FOUND = "User not found";
    private final ChatRepository chatRepository;
    private final AdvertRepository advertRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public Long getChat(Long advertId, Long userId, Principal principal) {
        User principalUser =  userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Advert advert = advertRepository.getReferenceById(advertId);
        if (userId == null) {
            return getChat(advert, principalUser);
        }
        if (!principalUser.getId().equals(advert.getUser().getId())) {
            throw new ChatException("You can't request a chat that doesn't belong to you.");
        }
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return getChat(advert, buyer);
    }

    private Long getChat(Advert advert, User user) {
        if (user.getId().equals(advert.getUser().getId())) {
            throw new ChatException("You can't chat with yourself.");
        }
        Chat chat;
        if (!chatRepository.existsByAdvert_IdAndBuyer_Id(advert.getId(), user.getId())) {
            chat = Chat.builder()
                    .advert(advert)
                    .buyer(user)
                    .build();
            chatRepository.save(chat);
        }
        chat = chatRepository.findChatByAdvert_IdAndBuyer_Id(advert.getId(), user.getId()).orElseThrow();
        return chat.getId();
    }

    public String sendMessage(Long chatId, String text, Principal principal) {
        Chat chat = chatRepository.getReferenceById(chatId);
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        if (!chat.getBuyer().getId().equals(user.getId()) && !chat.getAdvert().getUser().getId().equals(user.getId())) {
            throw new ChatException("It's not your chat");
        }
        Message message = Message.builder()
                .chat(chat)
                .text(text)
                .sender(user)
                .build();
        messageRepository.save(message);
        return "Successful send";
    }

    public Page<ChatDto> getChats(int pageNo, int pageSize, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Chat> page = chatRepository.findChatsByBuyer_IdOrAdvert_User_Id(user.getId(), user.getId(), pageable);

        return page.map(chatMapper::toDto);
    }

    public Page<MessageDto> getChatMessages(int pageNo, int pageSize, Long chatId) {
        Sort sort = Sort.by("date").ascending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Message> page = messageRepository.findAllByChat_Id(chatId, pageable);
        return page.map(messageMapper::toDto);
    }
}
