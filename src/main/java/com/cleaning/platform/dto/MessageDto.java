package com.cleaning.platform.dto;
import lombok.*;
@Getter @Setter
public class MessageDto {
    private String senderEmail;
    private String receiverId;
    private String content;
    private String relatedBookingId;
}