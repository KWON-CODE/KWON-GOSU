package com.cleaning.platform.dto;
import com.cleaning.platform.domain.ServiceCategory;
import lombok.*;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

@Getter @Setter
public class BookingDto {
    private String userId;
    private String providerId;
    private ServiceCategory serviceCategory;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime desiredDateTime;
    private String zipcode;
    private String mainAddress;
    private String detailAddress;
    private Integer quotedPrice;

    private String postId;

}