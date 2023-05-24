package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.MessageDto;
import com.pokotilov.finaltask.services.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Чат", description = "Методы для работы с чатом.")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody Long advertId, Principal principal) {
        return ResponseEntity.ok(chatService.createChat(advertId, principal).getMessage());
    }

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(chatService.sendMessage(messageDto).getMessage());
    }
}
