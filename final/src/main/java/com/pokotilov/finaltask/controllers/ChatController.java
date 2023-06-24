package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.services.chat.ChatService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Чат", description = "Методы для работы с чатом.")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chats")
    public Page<ChatDto> getChats(
            @Parameter(description = "№ страницы. (1..N)", required = true) @RequestParam(defaultValue = "1")
            @NotNull @Positive Integer pageNo,
            @Parameter(description = "Размер страницы.", required = true) @RequestParam(defaultValue = "10")
            @NotNull @Positive Integer pageSize,
            Principal principal) {
        return chatService.getChats(pageNo, pageSize, principal);
    }

    @GetMapping("/chats/{advertId}")
    public ChatDto getChat(
            @Parameter(description = "Id объявление в котором вы хотите получить чат.") @PathVariable("advertId")
            @Positive Long advertId,
            @Parameter(description = "Если вы являетесь владельцем объявления, вам так же нужно указать с каким пользователем чат вы хотите получить")
            @RequestParam(required = false) @Nullable @Positive Long userId,
            Principal principal) {
        return chatService.getChat(advertId, userId, principal);
    }

    @PostMapping("/chats/{chatId}/messages")
    public MessageDto sendMessage(
            @Parameter(description = "Id чата.") @PathVariable("chatId") @Positive Long chatId,
            @Parameter(description = "Текст сообщения.") @RequestParam @NotBlank String text,
            Principal principal) {
        return chatService.sendMessage(chatId, text, principal);
    }

    @GetMapping("/chats/{chatId}/messages")
    public Page<MessageDto> getChatMessages(
            @Parameter(description = "№ страницы.", required = true) @RequestParam(defaultValue = "1") @NotNull @Positive Integer pageNo,
            @Parameter(description = "Размер страницы.", required = true) @RequestParam(defaultValue = "10") @NotNull @Positive Integer pageSize,
            @Parameter(description = "ID чата.", required = true) @PathVariable @Positive Long chatId, Principal principal) {
        return chatService.getChatMessages(pageNo, pageSize, chatId, principal);
    }
}
