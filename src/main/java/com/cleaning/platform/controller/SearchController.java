package com.cleaning.platform.controller;

import com.cleaning.platform.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("posts", searchService.searchPosts(keyword));
        model.addAttribute("providers", searchService.searchProviders(keyword));
        return "search-result";
    }
}