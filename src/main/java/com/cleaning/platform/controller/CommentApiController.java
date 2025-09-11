package com.cleaning.platform.controller;

import com.cleaning.platform.dto.CommentDto;
import com.cleaning.platform.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentApiController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<?> createComment(@PathVariable String postId,
                                           @Valid @RequestBody CommentDto dto,
                                           @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        postService.createComment(postId, currentUser.getUsername(), dto);
        return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 등록되었습니다."));
    }


}