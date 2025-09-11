// ProviderDto.java
package com.cleaning.platform.dto;

import com.cleaning.platform.domain.ProviderType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProviderDto {
    private String providerName;
    private String businessRegistrationNumber;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String description;
    private ProviderType providerType;
}