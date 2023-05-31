package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.ChatDto;
import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.services.ChatService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Чат", description = "Методы для работы с чатом.")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<Long> getChat(
            @Parameter(description = "Id объявления в котором вы хотите получить чат.") @RequestParam Long advertId,
            @Parameter(description = "Если вы являетесь владельцем объявления, вам так же нужно указать с каким пользователем чат вы хотите получить")
            @RequestParam(required = false) @Nullable Long userId,
            Principal principal) {
        return ResponseEntity.ok().body(chatService.getChat(advertId, userId, principal));
    }

    @PostMapping("/message")
    public ResponseEntity<String> sendMessage(
            @Parameter(description = "Id чата.") @RequestParam Long chatId,
            @Parameter(description = "Текст сообщения.") @RequestParam @NotBlank String text,
            Principal principal) {
        return ResponseEntity.ok().body(chatService.sendMessage(chatId, text, principal));
    }

    @GetMapping("/all")
    public Page<ChatDto> getChats(
            @Parameter(description = "№ страницы.", required = true) @RequestParam int pageNo,
            @Parameter(description = "Размер страницы.", required = true) @RequestParam int pageSize,
            Principal principal) {
        return chatService.getChats(pageNo, pageSize, principal);
    }

    @GetMapping("/{chatId}/messages")
    public Page<MessageDto> getChatMessages(@Parameter(description = "№ страницы.", required = true) @RequestParam int pageNo,
                                            @Parameter(description = "Размер страницы.", required = true) @RequestParam int pageSize,
                                            @Parameter(description = "ID чата.", required = true) @PathVariable Long chatId) {
        return chatService.getChatMessages(pageNo, pageSize, chatId);
    }
}
