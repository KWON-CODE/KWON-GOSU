package com.cleaning.platform.controller;

import com.cleaning.platform.domain.Users;
import com.cleaning.platform.dto.UserDto;
import com.cleaning.platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    @GetMapping("")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "user-list";
    }

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;

        if (session != null && session.getAttribute("errorMessage") != null) {
            errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
        }
        
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "login-form";
    }

    @GetMapping("/new")
    public String userForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("pageTitle", "회원 가입");
        return "user-form";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("userDto") UserDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user-form";
        }


        userService.registerUser(dto);
        return "redirect:/users";
    }

    @GetMapping("/edit/{userId}")
    public String userEditForm(@PathVariable String userId, Model model) {
        Users users = userService.findUserByIdOrThrow(userId);
        if (users == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }


        UserDto dto = new UserDto();
        dto.setUsername(users.getUsername());
        dto.setEmail(users.getEmail()); // 이메일 수정 불가
        dto.setPhoneNumber(users.getPhoneNumber());

        if (users.getAddress() != null) {
            dto.setZipcode(users.getAddress().getZipcode());
            dto.setMainAddress(users.getAddress().getMainAddress());
            dto.setDetailAddress(users.getAddress().getDetailAddress());
        }

        model.addAttribute("userId", userId);
        model.addAttribute("userDto", dto);
        return "user-edit-form";
    }

    @PostMapping("/edit/{userId}")
    public String updateUser(@PathVariable String userId,
                             @Valid @ModelAttribute("userDto") UserDto dto,
                             BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors("username")) {
            return "user-edit-form";
        }

        userService.updateUser(userId, dto);
        return "redirect:/users";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {

       try {
           boolean success = userService.verifyEmail(token);

           if (success) {
               redirectAttributes.addFlashAttribute("message", "이메일 인증이 성공적으로 완료되었습니다. 이제 로그인할 수 있습니다.");
           } else {
               redirectAttributes.addFlashAttribute("message", "유효하지 않은 인증 링크입니다.");
           }
       } catch (Exception e) {
           System.err.println("======================================================");
           System.err.println("!!! verifyEmail 처리 중 심각한 예외 발생 !!!");
           e.printStackTrace(); // 예외의 전체 내용을 콘솔에 출력
           System.err.println("======================================================");
           redirectAttributes.addFlashAttribute("message", "인증 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.");
        }

        return "redirect:/users/login";
    }

    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<?> checkEmailDuplication(@RequestParam("email") String email) {
        boolean isDuplicated = userService.isEmailDuplicated(email);
        return ResponseEntity.ok().body(java.util.Map.of("isDuplicated", isDuplicated));
    }
}
