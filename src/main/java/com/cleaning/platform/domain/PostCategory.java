package com.cleaning.platform.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {

    RECRUIT("우리동네 전문가"),
    SELL("판매"),
    BUY("구매"),
    FREE("자유");

    private final String displayName;
}