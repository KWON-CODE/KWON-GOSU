package com.cleaning.platform.dto;

import com.cleaning.platform.domain.Message;
import com.cleaning.platform.domain.Users;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MessageResponseDto {
    private String content;
    private LocalDateTime sentAt;
    private SenderDto sender;


    public MessageResponseDto(Message message) {
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
        this.sender = new SenderDto(message.getSender());
    }

    @Getter
    public  static class SenderDto {
        private String id;
        private String username;
        private String email;

        public SenderDto(Users user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }
}