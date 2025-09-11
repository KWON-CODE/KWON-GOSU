package com.cleaning.platform.controller;

import com.cleaning.platform.domain.Post;
import com.cleaning.platform.domain.PostCategory;
import com.cleaning.platform.dto.CommentDto;
import com.cleaning.platform.dto.PostDto;
import com.cleaning.platform.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public String postList(Model model,
       @RequestParam(value = "page", defaultValue = "0") int page,
       @RequestParam(value = "keyword", defaultValue = "") String keyword,
       @RequestParam(value = "category", required = false) PostCategory category) {
        Page<Post> paging = postService.findPosts(keyword, category, page);

        String boardTitle = (category != null) ? category.getDisplayName() + " 게시판" : "전체 게시판";

        model.addAttribute("boardTitle", boardTitle);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("categories", PostCategory.values());
        return "post-list";
    }

    @GetMapping("/new")
    public String postForm(Model model,
    @RequestParam(value = "category", required = true) PostCategory category) {
        PostDto postDto = new PostDto();
        postDto.setCategory(category);

        model.addAttribute("postDto", postDto);
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("category", category);
        return "post-form";
    }

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "category") PostCategory category,
                             @RequestParam(value = "price", required = false, defaultValue = "0") int price,
                             @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                             Model model) throws IOException {

        System.out.println("===== [CONTROLLER] CreatePost 시작 =====");
        System.out.println("===== [CONTROLLER] RequestParam imageFiles: " + (imageFiles != null ? imageFiles.size() : "null") + " =====");

        System.out.println("===== [CONTROLLER] RequestParam category: " + category);

        // 입력값 검증
        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("error", "제목을 입력해주세요.");
            model.addAttribute("postDto", new PostDto());
            model.addAttribute("categories", PostCategory.values());
            return "post-form";
        }

        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("error", "내용을 입력해주세요.");
            model.addAttribute("postDto", new PostDto());
            model.addAttribute("categories", PostCategory.values());
            return "post-form";
        }

        PostDto dto = new PostDto();
        dto.setTitle(title.trim());
        dto.setContent(content.trim());
        dto.setCategory(category);
        dto.setImageFiles(imageFiles);
        dto.setPrice(price);

        System.out.println("===== [CONTROLLER] PostDto 생성 완료, Files count: " +
                (dto.getImageFiles() != null ? dto.getImageFiles().size() : 0) + " =====");

        postService.createPost(user.getUsername(), dto);

        if (category != null) {
            return "redirect:/posts?category=" + category.name();
        } else {
            return "redirect:/posts";
        }
    }

    @GetMapping("/{postId}")
    public String postDetail(@PathVariable String postId, Model model,
                             HttpServletRequest request, HttpServletResponse response) {

        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {

            if (!oldCookie.getValue().contains("[" + postId + "]")) {
                postService.incrementViewCount(postId);
                oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            postService.incrementViewCount(postId);
            Cookie newCookie = new Cookie("postView", "[" + postId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }

        model.addAttribute("post", postService.findPostByIdOrThrow(postId));
        model.addAttribute("commentDto", new CommentDto());
        return "post-detail";
    }

    @PostMapping("/{postId}/comments")
    public String createComment(@PathVariable String postId,
                                @Valid @ModelAttribute("commentDto") CommentDto dto, BindingResult bindingResult,
                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", postService.findPostByIdOrThrow(postId));
            return "post-detail";
        }
        postService.createComment(postId, user.getUsername(), dto);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable String postId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        postService.deletePost(postId, user.getUsername());
        return "redirect:/posts";
    }


    @GetMapping("/more")
    public String getMorePosts(Model model,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "keyword", defaultValue = "") String keyword,
                               @RequestParam(value = "category", required = false) PostCategory category) {

        Page<Post> paging = postService.findPosts(keyword, category, page);
        model.addAttribute("paging", paging);

        return "fragments/post-cards";
    }
}