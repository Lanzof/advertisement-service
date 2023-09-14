package com.pokotilov.finaltask.services.email;

import jakarta.mail.MessagingException;

import java.util.Locale;
import java.util.Map;

public interface EmailService {
    void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException;

    void sendMessageUsingThymeLeafTemplate(String to, String subject, Map<String, Object> templateModel, Locale locale) throws MessagingException;
}
