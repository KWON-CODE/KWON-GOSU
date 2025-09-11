package com.cleaning.platform.controller;

import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.dto.BookingDto;
import com.cleaning.platform.service.BookingService;
import com.cleaning.platform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cleaning.platform.domain.Post;
import com.cleaning.platform.service.PostService;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final PostService postService;


    @GetMapping("")
    public String bookingList(Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("bookings", bookingService.findBookingsByUser(currentUser.getUsername()));
        return "booking-list";
    }


    @GetMapping("/new")
    public String bookingForm(Model model, @AuthenticationPrincipal User currentUser,
                              @RequestParam(name = "postId", required = false) String postId,
                              @RequestParam(name = "providerId", required = false) String providerId) {
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        BookingDto bookingDto = new BookingDto();

        if (postId != null) {
            Post post = postService.findPostByIdOrThrow(postId);
            model.addAttribute("post", post);
            bookingDto.setPostId(post.getId());
            bookingDto.setProviderId(post.getUsers().getId());
            bookingDto.setQuotedPrice(post.getPrice());

        } else {
            List<ServiceProvider> providers = userService.findAllProviders();
            model.addAttribute("providers", providers);
            try {
                List<Map<String, Object>> providerData = providers.stream()
                        .map(provider -> Map.of("id", (Object) provider.getId(), "providerName", provider.getProviderName()))
                        .collect(Collectors.toList());
                String providersJson = new ObjectMapper().writeValueAsString(providerData);
                model.addAttribute("providersJson", providersJson);
            } catch (Exception e) {
                model.addAttribute("providersJson", "[]");
            }
        }

        model.addAttribute("bookingDto", bookingDto);

        return "booking-form";
    }


    @PostMapping("/new")
    public String createBooking(@ModelAttribute BookingDto dto, @AuthenticationPrincipal User currentUser) {
        dto.setUserId(currentUser.getUsername());
        bookingService.createBooking(dto);
        return "redirect:/bookings";
    }
}