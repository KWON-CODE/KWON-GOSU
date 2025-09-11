package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    @Column(name = "booking_id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProvider provider;

    // ↓↓↓↓ 누락된 필드 추가 ↓↓↓↓
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moving_service_id")
    private MovingService movingService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ac_service_id")
    private AcService acService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_category", nullable = false)
    private ServiceCategory serviceCategory;

    @Column(name = "desired_date_time", nullable = false)
    private LocalDateTime desiredDateTime;

    @Embedded
    private Address address;


    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "quoted_price")
    private Integer quotedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private Review review;

    @Builder
    public Booking(String id, Users users, ServiceProvider provider, MovingService movingService, AcService acService,
                   Post post,ServiceCategory serviceCategory, LocalDateTime desiredDateTime, Address address,
                   String requirements, Integer quotedPrice) {
        this.id = id;
        this.users = users;
        this.provider = provider;
        this.movingService = movingService;
        this.acService = acService;
        this.post = post;
        this.serviceCategory = serviceCategory;
        this.desiredDateTime = desiredDateTime;
        this.address = address;
        this.requirements = requirements;
        this.quotedPrice = quotedPrice;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}