package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @Column(name = "comment_id")
    private String id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Comment(String id, Post post, Users users, String content) {
        this.id = id;
        this.post = post;
        this.users = users;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}