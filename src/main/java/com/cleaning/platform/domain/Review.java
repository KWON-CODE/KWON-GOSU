package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REVIEWS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @Column(name = "review_id", length=50)
    private String id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Review(String id, Booking booking, Users users, ServiceProvider provider, int rating, String comment) {
        this.id = id;
        this.booking = booking;
        this.users = users;
        this.provider = provider;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public void update(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
        this.updatedAt = LocalDateTime.now();
    }
}