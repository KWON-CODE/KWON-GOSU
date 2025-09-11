package com.cleaning.platform.dto;

import com.cleaning.platform.domain.Users;
import lombok.Getter;

@Getter
public class ChatUserDto {
    private String id;
    private String username;
    private String email;

    public ChatUserDto(Users user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}