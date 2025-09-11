package com.cleaning.platform.dto;

import com.cleaning.platform.domain.AcProductType;
import com.cleaning.platform.domain.AcServiceType;
import lombok.*;

@Getter @Setter
public class AcServiceDto {


    private AcServiceType serviceType;
    private AcProductType productType;
    private Integer priceRangeMin;
    private Integer priceRangeMax;
    private String description;
}