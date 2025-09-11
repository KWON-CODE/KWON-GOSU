package com.cleaning.platform.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentVerificationRequest {
    private String imp_uid;
    private String merchant_uid;
}