package com.cleaning.platform.dto;

import com.cleaning.platform.domain.MovingType;
import lombok.*;

@Getter @Setter
public class MovingServiceDto {
    private MovingType movingType;
    private String capacity;
    private Integer basePrice;
    private String additionalInfo;
}