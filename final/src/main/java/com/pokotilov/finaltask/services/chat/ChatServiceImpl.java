package com.pokotilov.finaltask.services.chat;

import com.pokotilov.finaltask.aop.LogExecution;
import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Chat;
import com.pokotilov.finaltask.entities.Message;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.NotFoundException;
import com.pokotilov.finaltask.exceptions.UnprocessableEntityException;
import com.pokotilov.finaltask.mapper.ChatMapper;
import com.pokotilov.finaltask.mapper.MessageMapper;
import com.pokotilov.finaltask.repositories.ChatRepository;
import com.pokotilov.finaltask.repositories.MessageRepository;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@LogExecution
public class ChatServiceImpl implements ChatService {

    private final AdvertService advertService;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    @Override
    public ChatDto getChat(Long advertId, Long userId, Principal principal) {
        User principalUser =  userService.getUserByPrincipal(principal);
        Advert advert = advertService.getAdvertById(advertId);
        if (userId == null) {
            return getChat(advert, principalUser);
        }
        if (!principalUser.getId().equals(advert.getUser().getId())) {
            throw new UnprocessableEntityException("You can't request a chat that doesn't belong to you.");
        }
        User buyer = userService.getUserById(userId);
        return getChat(advert, buyer);
    }

    private ChatDto getChat(Advert advert, User user) {
        if (user.getId().equals(advert.getUser().getId())) {
            throw new UnprocessableEntityException("You can't chat with yourself.");
        }
        Chat chat;
        if (!chatRepository.existsByAdvert_IdAndBuyer_Id(advert.getId(), user.getId())) {
            chat = Chat.builder()
                    .advert(advert)
                    .buyer(user)
                    .build();
            chat = chatRepository.save(chat);
        } else {
            chat = chatRepository.findChatByAdvert_IdAndBuyer_Id(advert.getId(), user.getId()).orElseThrow();
        }
        return chatMapper.toDto(chat);
    }

    @Override
    public MessageDto sendMessage(Long chatId, String text, Principal principal) {
        Chat chat = getChatById(chatId);
        User user = userService.getUserByPrincipal(principal);
        if (!chat.getBuyer().getId().equals(user.getId()) && !chat.getAdvert().getUser().getId().equals(user.getId())) {
            throw new UnprocessableEntityException("It's not your chat");
        }
        Message message = Message.builder()
                .chat(chat)
                .text(text)
                .sender(user)
                .build();
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public Page<ChatDto> getChats(Integer pageNo, Integer pageSize, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Chat> page = chatRepository.findChatsByBuyer_IdOrAdvert_User_Id(user.getId(), user.getId(), pageable);
        return page.map(chatMapper::toDto);
    }

    @Override
    public Page<MessageDto> getChatMessages(Integer pageNo, Integer pageSize, Long chatId, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Chat chat = getChatById(chatId);
        if (!chat.getBuyer().getId().equals(user.getId()) && !chat.getAdvert().getUser().getId().equals(user.getId())) {
            throw new UnprocessableEntityException("It's not your chat");
        }
        Sort sort = Sort.by("date").descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Message> page = messageRepository.findAllByChat_Id(chatId, pageable);
        return page.map(messageMapper::toDto);
    }

    private Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(() -> new NotFoundException("Chat not found"));
    }
}
