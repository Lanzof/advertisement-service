package com.pokotilov.finaltask.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EmailLocalization {
    private final MessageSource messageSource;

    @Autowired
    public EmailLocalization(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getSubject() {
        return messageSource.getMessage("email.subject", null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String name) {
        return messageSource.getMessage("email.message", new Object[]{name}, LocaleContextHolder.getLocale());
    }
}
