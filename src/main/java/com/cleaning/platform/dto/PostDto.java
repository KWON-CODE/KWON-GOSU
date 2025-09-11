package com.cleaning.platform.dto;
import com.cleaning.platform.domain.PostCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PostDto {
    private String title;
    private String content;
    private PostCategory category;
    private List<MultipartFile> imageFiles;
    private int price;

    // 생성자
    public PostDto() {
        this.imageFiles = new ArrayList<>();
    }
}