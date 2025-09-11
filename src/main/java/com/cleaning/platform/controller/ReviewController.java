package com.cleaning.platform.controller;

import com.cleaning.platform.dto.ReviewDto;
import com.cleaning.platform.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final BookingService bookingService;

    @GetMapping("")
    public String reviewList(Model model) {
        model.addAttribute("reviews", bookingService.findAllReviews());
        return "review-list";
    }

    @GetMapping("/new/{bookingId}")
    public String reviewForm(@PathVariable String bookingId, Model model) {
        model.addAttribute("reviewDto", new ReviewDto());
        model.addAttribute("booking", bookingService.findBookingByIdOrThrow(bookingId));
        return "review-form";
    }

    @PostMapping("/new/{bookingId}")
    public String createReview(@PathVariable String bookingId,
                               @Valid @ModelAttribute("reviewDto") ReviewDto dto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal User currentUser,
                               Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("booking", bookingService.findBookingByIdOrThrow(bookingId));
            return "review-form";
        }


        bookingService.createReview(bookingId, currentUser.getUsername(), dto);

        return "redirect:/reviews";
    }
}