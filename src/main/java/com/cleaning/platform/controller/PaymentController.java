package com.cleaning.platform.controller;

import com.cleaning.platform.domain.Booking;
import com.cleaning.platform.dto.PaymentDto;
import com.cleaning.platform.service.BookingService;
import com.cleaning.platform.service.PaymentService;
import com.cleaning.platform.dto.PaymentVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    @GetMapping("")
    public String paymentList(Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("payments", paymentService.findPaymentsByUser(currentUser.getUsername()));
        return "payment-list";
    }

    @GetMapping("/new/{bookingId}")
    public String paymentForm(@PathVariable String bookingId, Model model, @AuthenticationPrincipal User currentUser) {
        Booking booking = bookingService.findBookingByIdOrThrow(bookingId);


        if (booking == null) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }
        if (!booking.getUsers().getEmail().equals(currentUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신의 예약에 대해서만 결제할 수 있습니다.");
        }

        model.addAttribute("paymentDto", new PaymentDto());
        model.addAttribute("booking", booking);
        return "payment-form";
    }


    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerificationRequest request,
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            paymentService.verifyAndSavePayment(
                    request.getImp_uid(),
                    request.getMerchant_uid(),
                    currentUser.getUsername()
            );
            return ResponseEntity.ok().body("결제가 성공적으로 검증되었습니다.");
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
