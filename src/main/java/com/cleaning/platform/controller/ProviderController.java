package com.cleaning.platform.controller;

import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.dto.AcServiceDto;
import com.cleaning.platform.dto.MovingServiceDto;
import com.cleaning.platform.service.BookingService;
import com.cleaning.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final UserService userService;
    private final BookingService bookingService;


    @GetMapping("/dashboard")
    public String providerServiceDashboard(@AuthenticationPrincipal User currentUser, Model model) {

        if (currentUser == null) {
            return "redirect:/users/login";
        }


        ServiceProvider provider = userService.findProviderByUserEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "서비스 제공자만 접근 가능합니다."));


        ServiceProvider providerWithServices = userService.findProviderById(provider.getId());

        model.addAttribute("provider", providerWithServices);


        model.addAttribute("provider", provider);

        return "provider-service-list";
    }


    @GetMapping("/ac-services/new")
    public String acServiceForm(@AuthenticationPrincipal User currentUser, Model model) {
        ServiceProvider provider = findProviderOrThrow(currentUser);
        model.addAttribute("providerId", provider.getId());
        model.addAttribute("acServiceDto", new AcServiceDto());
        return "ac-service-form";
    }


    @PostMapping("/ac-services/new")
    public String createAcService(@AuthenticationPrincipal User currentUser, @ModelAttribute AcServiceDto dto) {
        ServiceProvider provider = findProviderOrThrow(currentUser);
        bookingService.createAcService(provider.getId(), dto);
        return "redirect:/provider/dashboard";
    }


    @GetMapping("/moving-services/new")
    public String movingServiceForm(@AuthenticationPrincipal User currentUser, Model model) {
        ServiceProvider provider = findProviderOrThrow(currentUser);
        model.addAttribute("providerId", provider.getId());
        model.addAttribute("movingServiceDto", new MovingServiceDto());
        return "moving-service-form";
    }


    @PostMapping("/moving-services/new")
    public String createMovingService(@AuthenticationPrincipal User currentUser, @ModelAttribute MovingServiceDto dto) {
        ServiceProvider provider = findProviderOrThrow(currentUser);
        bookingService.createMovingService(provider.getId(), dto);
        return "redirect:/provider/dashboard";
    }


    private ServiceProvider findProviderOrThrow(User currentUser) {
        return userService.findProviderByUserEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "서비스 제공자만 접근 가능한 페이지입니다."));
    }
}