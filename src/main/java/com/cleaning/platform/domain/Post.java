package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "POSTS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @Column(name = "post_id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "price")
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private PostStatus status = PostStatus.OPEN;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PostCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostImage> images = new ArrayList<>();


    public void incrementViewCount() {
        this.viewCount++;
    }
    public PostCategory getCategory()
    {

        return this.category;
    }


    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }


    public String getThumbnailUrl() {
        if (this.thumbnailUrl != null && !this.thumbnailUrl.isEmpty()) {
            return this.thumbnailUrl;
        }

        if (images != null && !images.isEmpty()) {
            return images.get(0).getImageUrl();
        }

        return null;
    }

    public void setThumbnailUrl(String thumbnailUrl) {

        this.thumbnailUrl = thumbnailUrl;
    }
}