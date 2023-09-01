package com.pokotilov.finaltask.kafka;

import com.pokotilov.finaltask.services.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GreetingsListener {
    private final EmailService emailService;

    @KafkaListener(topics = "greetings", groupId = "1")
    void listener(String data) {
        String[] array = data.split(";");

        Map<String, Object> params = new HashMap<>();
        params.put("recipientName", array[1]);
        params.put("text", "greeting text");
        params.put("senderName", "my company");

        try {
            emailService.sendMessageUsingThymeLeafTemplate(array[0], "Greetings", params);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
