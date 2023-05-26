package com.pokotilov.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long chatId;
    private Long id;
    @NotBlank
    private String text;
    private LocalDateTime date;
    private String senderName;
}
