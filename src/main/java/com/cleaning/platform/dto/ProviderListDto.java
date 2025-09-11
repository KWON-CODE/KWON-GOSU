package com.cleaning.platform.dto;

import com.cleaning.platform.domain.ProviderType;
import com.cleaning.platform.domain.ServiceProvider;
import lombok.Getter;

@Getter
public class ProviderListDto {
    private String id;
    private String providerName;
    private String contactPhone;
    private ProviderType providerType;
    private String profileImageName;
    private String externalPlaceUrl;
    private Float averageRating;
    private Integer naverVisitorReviewCount;
    private Integer trustScore;

    public ProviderListDto(ServiceProvider entity) {
        this.id = entity.getId();
        this.providerName = entity.getProviderName();
        this.contactPhone = entity.getContactPhone();
        this.providerType = entity.getProviderType();
        this.profileImageName = entity.getProfileImageName();
        this.externalPlaceUrl = entity.getExternalPlaceUrl();
        this.averageRating = entity.getAverageRating();
        this.naverVisitorReviewCount = entity.getNaverVisitorReviewCount();
        this.trustScore = entity.getTrustScore();
    }
}