package com.cleaning.platform.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
public class PageResponseDto<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final int totalPages;
    private final long totalElements;
    private final boolean last;

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.last = page.isLast();
    }
}