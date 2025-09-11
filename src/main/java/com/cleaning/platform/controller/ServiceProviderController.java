package com.cleaning.platform.controller;

import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.dto.ProviderRegistrationDto; // DTO 변경
import com.cleaning.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final UserService userService;


    @GetMapping("")
    public String providerList(Model model) {
        model.addAttribute("providers", userService.findAllProviders());
        return "provider-list";
    }


    @GetMapping("/new")
    public String providerRegistrationForm(Model model) {
        model.addAttribute("providerDto", new ProviderRegistrationDto());
        return "provider-form";
    }


    @PostMapping("/new")
    public String registerProvider(
            @Valid @ModelAttribute("providerDto") ProviderRegistrationDto dto,
                                   BindingResult bindingResult, RedirectAttributes redirectAttributes)
            throws IOException {
        if (bindingResult.hasErrors()) {
            System.out.println("### 유효성 검사 오류 발생! ###");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.toString());
            });


            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.providerDto",
                    bindingResult);



            redirectAttributes.addFlashAttribute("providerDto", dto);


            redirectAttributes.addFlashAttribute("error",
                    "입력 내용을 다시 확인해주세요.");
            return "redirect:/providers/new";


        }  try {

            userService.registerProvider(dto);
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/providers/new";
        }


        redirectAttributes.addFlashAttribute("success",
                "서비스 제공자 가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/users/login";
    }

    @GetMapping("/{providerId}")
    public String providerDetail(@PathVariable String providerId, Model model) {
        ServiceProvider provider = userService.findProviderById(providerId);
        if (provider == null) {
            throw new IllegalArgumentException("존재하지 않는 서비스 제공자입니다.");
        }
        model.addAttribute("provider", provider);
        return "provider-detail";
    }
}