package com.cleaning.platform.dto;
import lombok.*;
@Getter @Setter
public class PaymentDto {
    private int amount;
    private String paymentMethod;
}