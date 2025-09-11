package com.cleaning.platform.controller;

import com.cleaning.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicated = userService.isUsernameDuplicated(username);
        return Map.of("duplicated", isDuplicated);
    }

    @GetMapping(value="/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean isDuplicated = userService.isEmailDuplicated(email);

        return ResponseEntity.ok(Map.of("duplicated", isDuplicated));
    }
}