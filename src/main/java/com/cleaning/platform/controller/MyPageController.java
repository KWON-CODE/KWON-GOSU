package com.cleaning.platform.controller;

import com.cleaning.platform.domain.Users;
import com.cleaning.platform.dto.UserDto;
import com.cleaning.platform.service.BookingService;
import com.cleaning.platform.service.PostService;
import com.cleaning.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    private final PostService postService;
    private final BookingService bookingService;

    @GetMapping("/profile")
    public String myProfileForm(@AuthenticationPrincipal User currentUser, Model model) {
        Users user = userService.findUserByEmailOrThrow(currentUser.getUsername());

        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        if (user.getAddress() != null) {
            dto.setZipcode(user.getAddress().getZipcode());
            dto.setMainAddress(user.getAddress().getMainAddress());
            dto.setDetailAddress(user.getAddress().getDetailAddress());
        }

        model.addAttribute("userDto", dto);
        return "my-profile-form";
    }

    @PostMapping("/profile")
    public String updateMyProfile(@Valid @ModelAttribute("userDto") UserDto dto,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal User currentUser,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasFieldErrors("username") || bindingResult.hasFieldErrors("phoneNumber")) {
            return "my-profile-form";
        }

        userService.updateUserByEmail(currentUser.getUsername(), dto);

        redirectAttributes.addFlashAttribute("success", "회원 정보가 성공적으로 수정되었습니다.");
        return "redirect:/my/profile";
    }

    @GetMapping("/dashboard")
    public String myDashboard(@AuthenticationPrincipal User currentUser, Model model) {
        String email = currentUser.getUsername();
        model.addAttribute("user", userService.findUserByEmailOrThrow(email));
        model.addAttribute("posts", postService.findPostsByUserEmail(email));
        model.addAttribute("bookings", bookingService.findBookingsByUser(email));
        return "my-dashboard";
    }
}