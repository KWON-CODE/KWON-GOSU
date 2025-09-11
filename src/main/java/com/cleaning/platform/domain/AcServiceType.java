package com.cleaning.platform.domain;

public enum AcServiceType {

    SALE("판매"),
    INSTALLATION("설치"),
    REPAIR("수리"),
    CLEANING("청소");

    private final String displayName;


    AcServiceType(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}