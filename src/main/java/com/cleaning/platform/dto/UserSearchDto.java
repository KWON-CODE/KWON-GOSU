package com.cleaning.platform.dto;

import com.cleaning.platform.domain.Users;
import lombok.Getter;

@Getter
public class UserSearchDto {
    private String id;
    private String username;

    public UserSearchDto(Users user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}