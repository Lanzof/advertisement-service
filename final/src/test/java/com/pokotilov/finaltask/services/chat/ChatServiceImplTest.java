package com.pokotilov.finaltask.services.chat;

import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.Chat;
import com.pokotilov.finaltask.entities.Message;
import com.pokotilov.finaltask.entities.User;
import com.pokotilov.finaltask.exceptions.UnprocessableEntityException;
import com.pokotilov.finaltask.mapper.ChatMapper;
import com.pokotilov.finaltask.mapper.MessageMapper;
import com.pokotilov.finaltask.repositories.ChatRepository;
import com.pokotilov.finaltask.repositories.MessageRepository;
import com.pokotilov.finaltask.services.advert.AdvertService;
import com.pokotilov.finaltask.services.user.UserService;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {
    @Mock
    private AdvertService advertService;
    @Mock
    private UserService userService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatMapper chatMapper;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void getChat_whenSellerRequestsChat_ReturnsChatId() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(2L)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 1L;
        Advert advert = Advert.builder()
                .id(advertId)
                .user(principalUser)
                .build();
        when(advertService.getAdvertById(advertId)).thenReturn(advert);
        Long userId = 1L;
        User buyer = User.builder()
                .id(userId)
                .build();
        when(userService.getUserById(userId)).thenReturn(buyer);
        when(chatRepository.existsByAdvert_IdAndBuyer_Id(advertId, 1L))
                .thenReturn(true);
        Chat chat = Chat.builder()
                .id(3L)
                .build();
        when(chatRepository.findChatByAdvert_IdAndBuyer_Id(advertId, userId))
                .thenReturn(Optional.of(chat));

        // Act
        Long result = chatService.getChat(advertId, userId, principal);

        // Assert
        assertEquals(3L, result);
    }

    @Test
    void getChat_whenBuyerRequestsChat_ReturnsChatId() {
        // Arrange
        Principal principal = new UserPrincipal("test@example.com");
        User principalUser = User.builder()
                .id(2L)
                .build();
        when(userService.getUserByPrincipal(principal))
                .thenReturn(principalUser);
        Long advertId = 1L;
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(1L).build())
                .build();
        when(advertService.getAdvertById(advertId)).thenReturn(advert);
        when(chatRepository.existsByAdvert_IdAndBuyer_Id(advertId, principalUser.getId()))
                .thenReturn(false);
        when(chatRepository.save(any(Chat.class)))
                .thenAnswer(invocation -> {
                    Chat chat = invocation.getArgument(0);
                    chat.setId(3L);
                    return chat;
                });

        // Act
        Long result = chatService.getChat(advertId, null, principal);

        // Assert
        assertEquals(3L, result);
    }

    @Test
    void getChat_sellerAskWrongAdvert_shouldThrow() {
        // Arrange
        Long advertId = 1L;
        Long userId = 2L;
        User principalUser = User.builder()
                .id(2L)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        when(userService.getUserByPrincipal(principal)).thenReturn(principalUser);
        Advert advert = Advert.builder()
                .id(advertId)
                .user(User.builder().id(1L).build())
                .build();
        when(advertService.getAdvertById(advertId)).thenReturn(advert);

        // Act & Assert
        assertThrows(UnprocessableEntityException.class,
                () -> chatService.getChat(advertId, userId, principal), "You can't request a chat that doesn't belong to you.");
    }

    @Test
    void getChat_userTriesToChatWithThemselves_shouldThrow() {
        // Arrange
        Long advertId = 1L;
        User principalUser = User.builder()
                .id(2L)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        when(userService.getUserByPrincipal(principal)).thenReturn(principalUser);
        Advert advert = Advert.builder()
                .id(advertId)
                .user(principalUser)
                .build();
        when(advertService.getAdvertById(advertId)).thenReturn(advert);

        // Act & Assert
        assertThrows(UnprocessableEntityException.class,
                () -> chatService.getChat(advertId, null, principal), "You can't chat with yourself.");
    }

    @Test
    void sendMessage_validAdvertAndUser_returnSuccessfulSend() {
        // Arrange
        User principalUser = User.builder()
                .id(2L)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        when(userService.getUserByPrincipal(principal)).thenReturn(principalUser);
        Advert advert = Advert.builder()
                .id(1L)
                .user(User.builder().id(3L).build())
                .build();
        Long chatId = 1L;
        Chat chat = Chat.builder()
                .id(chatId)
                .advert(advert)
                .buyer(principalUser)
                .build();
        when(chatRepository.getReferenceById(chatId)).thenReturn(chat);
        String text = "Hello";
        when(messageRepository.save(any(Message.class))).thenReturn(null);

        // Act
        String result = chatService.sendMessage(chatId, text, principal);

        // Assert
        assertEquals("Successful send", result);
    }

    @Test
    void sendMessage_wrongChatId_shouldThrow() {
        // Arrange
        User principalUser = User.builder()
                .id(1L)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        when(userService.getUserByPrincipal(principal)).thenReturn(principalUser);
        Advert advert = Advert.builder()
                .id(1L)
                .user(User.builder().id(3L).build())
                .build();
        Long chatId = 1L;
        Chat chat = Chat.builder()
                .id(chatId)
                .advert(advert)
                .buyer(User.builder().id(2L).build())
                .build();
        when(chatRepository.getReferenceById(chatId)).thenReturn(chat);
        String text = "Hello";

        // Act & Assert
        assertThrows(UnprocessableEntityException.class,
                () -> chatService.sendMessage(chatId, text, principal), "It's not your chat");
    }

    @Test
    void getChats__ReturnsPageOfMessages() {
        // Arrange
        int page = 1;
        int size = 10;
        User principalUser = User.builder()
                .id(1L)
                .build();
        Principal principal = new UserPrincipal("test@example.com");
        when(userService.getUserByPrincipal(principal)).thenReturn(principalUser);
        List<Chat> chats = List.of(
                Chat.builder().id(1L).build(),
                Chat.builder().id(2L).build()
        );
        when(chatRepository.findChatsByBuyer_IdOrAdvert_User_Id(any(Long.class), any(Long.class), any()))
                .thenReturn(new PageImpl<>(chats));
        when(chatMapper.toDto(any(Chat.class))).thenAnswer(invocation -> {
            Chat chat = invocation.getArgument(0);
            return ChatDto.builder()
                    .id(chat.getId())
                    .build();
        });

        // Act
        Page<ChatDto> result = chatService.getChats(, principal);

        // Assert
        assertEquals(chats.get(0).getId(), result.getContent().get(0).getId());
        assertEquals(chats.get(1).getId(), result.getContent().get(1).getId());
    }

    @Test
    void getChatMessages_validUser_ReturnsPageOfMessages() {
        // Arrange
        Long chatId = 1L;
        int page = 1;
        int size = 10;
        List<Message> messages = List.of(
                Message.builder().id(1L).build(),
                Message.builder().id(2L).build(),
                Message.builder().id(3L).build()
        );
        Page<Message> messagesPage = new PageImpl<>(messages);
        when(messageRepository.findAllByChat_Id(any(Long.class), any()))
                .thenReturn(messagesPage);
        when(messageMapper.toDto(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            return MessageDto.builder()
                    .id(message.getId())
                    .build();
        });

        // Act
        Page<MessageDto> result = chatService.getChatMessages(page, size, chatId, );

        // Assert
        assertEquals(messages.get(0).getId(), result.getContent().get(0).getId());
    }
}