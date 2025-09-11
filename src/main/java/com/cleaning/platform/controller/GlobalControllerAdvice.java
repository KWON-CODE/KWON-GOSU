package com.cleaning.platform.controller;

import com.cleaning.platform.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MessageService messageService;

    @ModelAttribute("unreadMessageCount")
    public long addUnreadMessageCountToModel(@AuthenticationPrincipal User user) {
        if (user != null) {

            return messageService.getUnreadMessageCount(user.getUsername());
        }

        return 0;
    }
}