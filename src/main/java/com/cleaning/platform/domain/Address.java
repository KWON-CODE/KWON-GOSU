package com.cleaning.platform.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String zipcode;
    private String mainAddress;
    private String detailAddress;

    public Address(String zipcode, String mainAddress, String detailAddress) {
        this.zipcode = zipcode;
        this.mainAddress = mainAddress;
        this.detailAddress = detailAddress;
    }


    public String getFullAddress() {
        return String.format("(%s) %s, %s", zipcode, mainAddress, detailAddress);
    }
}