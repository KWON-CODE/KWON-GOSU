package com.cleaning.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SERVICES_PROVIDERS")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceProvider {

    @Id
    @Column(name = "provider_id", length = 50)
    private String id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼 지정
    @ToString.Exclude
    private Users users;

    @Column(name = "provider_name", nullable = false, length = 150)
    private String providerName;

    @Column(name="business_registration_number", length = 40, unique = true)
    private String businessRegistrationNumber;

    @Column(name="contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_phone", nullable = false, length = 30)
    private String contactPhone;

    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "average_rating")
    private Float averageRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "profile_image_name")
    private String profileImageName;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<AcService> acServices = new HashSet<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<MovingService> movingServices = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataSource dataSource = DataSource.REGISTERED;

    @Column(name = "external_rating")
    private Float externalRating;

    @Column(name = "external_review_count")
    private Integer externalReviewCount;

    @Column(name = "external_place_url")
    private String externalPlaceUrl;

    public enum DataSource {
        REGISTERED,
        CRAWLED
    }

    @Column(name = "naver_blog_review_count")
    private Integer naverBlogReviewCount;

    @Column(name = "naver_visitor_review_count")
    private Integer naverVisitorReviewCount;

    @Column(name = "trust_score")
    private Integer trustScore;

    @PostLoad
    @PrePersist
    @PreUpdate
    public void calculateAndSetTrustScore() {
        int score = 0;


        if (this.businessRegistrationNumber != null && !this.businessRegistrationNumber.isBlank()) {
            score += 15;
        }

        if (this.externalPlaceUrl != null && !this.externalPlaceUrl.isBlank()) {
            score += 10;
        }

        if (this.profileImageName != null && !this.profileImageName.isBlank()) {
            score += 5;
        }

        if (this.description != null && !this.description.isBlank()) {
            score += 5;
        }

        if (this.contactPerson != null && !this.contactPerson.isBlank()) {
            score += 3;
        }

        if (this.contactPhone != null && !this.contactPhone.equals("정보 없음") && !this.contactPhone.isBlank()) {
            score += 3;
        }

        if (this.naverVisitorReviewCount != null && this.naverVisitorReviewCount > 0) {
            score += Math.min(this.naverVisitorReviewCount, 20); // 최대 20점까지만 반영
        }

        if (this.naverBlogReviewCount != null && this.naverBlogReviewCount > 0) {
            score += Math.min(this.naverBlogReviewCount / 2, 10); // 2개당 1점, 최대 10점
        }

        if (this.averageRating != null && this.averageRating > 0.0f) {
            score += (int) (this.averageRating * 2);
        }

        this.trustScore = score;

        System.out.println("Trust score for '" + this.providerName + "' calculated: " + this.trustScore); // 디버깅용 로그
    }

    @Builder
    public ServiceProvider(String id, Users users, String providerName,
                           String businessRegistrationNumber,
                           String contactPhone, String contactEmail,
                           ProviderType providerType,
                           String profileImageName, String contactPerson,String description) {
        this.id = id;
        this.users = users;
        this.providerName = providerName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.averageRating = 0.0f;
        this.providerType = providerType;
        this.createdAt = LocalDateTime.now();
        this.profileImageName = profileImageName;
        this.contactPerson = contactPerson;
        this.description = description;
    }
    public void updateProfileImage(String profileImageName) {
        this.profileImageName = profileImageName;
    }
}