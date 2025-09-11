package com.cleaning.platform.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter @Setter
public class ReviewDto {

    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
    private int rating;

    @NotEmpty(message = "리뷰 내용을 입력해주세요.")
    private String comment;
}