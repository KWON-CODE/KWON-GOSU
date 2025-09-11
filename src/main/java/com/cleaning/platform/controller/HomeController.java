package com.cleaning.platform.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home() {

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {

        model.addAttribute("pageTitle", "대시보드");
        return "home";
    }

}
