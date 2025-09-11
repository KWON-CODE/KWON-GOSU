package com.cleaning.platform.service;

import com.cleaning.platform.domain.*;
import com.cleaning.platform.dto.CommentDto;
import com.cleaning.platform.dto.PostDto;
import com.cleaning.platform.repository.CommentRepository;
import com.cleaning.platform.repository.PostRepository;
import com.cleaning.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FileService fileService;


    @Transactional
    public void createPost(String userEmail, PostDto dto) {
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post.PostBuilder postBuilder  = Post.builder()
                .id("P-" + UUID.randomUUID().toString().substring(0, 7))
                .users(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .createdAt(LocalDateTime.now())
                .viewCount(0);


        if (dto.getCategory() == PostCategory.RECRUIT || dto.getCategory() == PostCategory.SELL) {
            postBuilder.price(dto.getPrice());
            postBuilder.status(PostStatus.OPEN);
        } else {
            postBuilder.price(0);

        }

        Post post = postBuilder.build();

        List<MultipartFile> imageFiles = dto.getImageFiles();
        System.out.println("===== [DEBUG] Received files: " + (imageFiles != null ? imageFiles.size() : "null") + " =====");

        if (imageFiles != null && !imageFiles.isEmpty()) {
            boolean isThumbnailSet = false;

            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);

                System.out.println("===== [DEBUG] Processing file " + (i+1) + ": " +
                        (file != null ? file.getOriginalFilename() : "null") + " =====");


                if (file == null) {
                    System.out.println("===== [DEBUG] File is null, skipping =====");
                    continue;
                }

                if (file.getSize() == 0) {
                    System.out.println("===== [DEBUG] File size is 0: " + file.getOriginalFilename() + " =====");
                    continue;
                }

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.trim().isEmpty()) {
                    System.out.println("===== [DEBUG] File name is empty or null, skipping =====");
                    continue;
                }

                String contentType = file.getContentType();
                System.out.println("===== [DEBUG] File details - Name: " + originalFilename +
                        ", Size: " + file.getSize() + ", ContentType: " + contentType + " =====");

                if (contentType == null || !isImageFile(contentType)) {
                    System.out.println("===== [DEBUG] Not an image file, skipping: " + contentType + " =====");
                    continue;
                }

                try {
                    String storedFilename = fileService.uploadFile(file);
                    String imageUrl = "/upload/" + storedFilename;

                    System.out.println("===== [DEBUG] File saved successfully: " + imageUrl + " =====");

                    PostImage postImage = PostImage.builder()
                            .imageUrl(imageUrl)
                            .build();

                    post.addImage(postImage);

                    if (!isThumbnailSet) {
                        post.setThumbnailUrl(imageUrl);
                        isThumbnailSet = true;
                        System.out.println("===== [DEBUG] Thumbnail set: " + imageUrl + " =====");
                    }

                } catch (Exception e) {
                    System.err.println("===== [ERROR] File upload failed: " + e.getMessage() + " =====");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("===== [DEBUG] No files to process =====");
        }

        Post savedPost = postRepository.save(post);
        System.out.println("===== [DEBUG] Post saved with ID: " + savedPost.getId() +
                ", Images count: " + savedPost.getImages().size() + " =====");
    }

    private boolean isImageFile(String contentType) {
        if (contentType == null) {
            return false;
        }

        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp") ||
                contentType.equals("image/bmp") ||
                contentType.equals("image/svg+xml") ||
                contentType.equals("image/tiff");
    }

    @Transactional
    public void createComment(String postId, String userEmail, CommentDto dto) {
        Post post = findPostByIdOrThrow(postId);
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Comment comment = Comment.builder()
                .id("C-" + UUID.randomUUID().toString().substring(0, 7))
                .post(post)
                .users(user)
                .content(dto.getContent())
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void deletePost(String postId, String userEmail) {
        Post post = findPostByIdOrThrow(postId);
        if (!post.getUsers().getEmail().equals(userEmail)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        postRepository.delete(post);
    }


    public Page<Post> findPosts(String keyword, PostCategory category, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, 8, Sort.by(sorts));

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
                return postRepository.findByCategoryAndKeyword(category, searchKeyword, pageable);
    }

    public Post findPostByIdOrThrow(String postId) {
        return postRepository.findPostWithDetailsById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보가 없습니다."));
    }

    public List<Post> findPostsByUserEmail(String email) {
        return postRepository.findByUsersEmailOrderByCreatedAtDesc(email);
    }

    @Transactional
    public void incrementViewCount(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        post.incrementViewCount();
    }
}