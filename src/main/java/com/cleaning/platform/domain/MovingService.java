package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MOVING_SERVICES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovingService {

    @Id
    @Column(name = "service_id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "moving_type", nullable = false)
    private MovingType movingType;

    @Column(length = 50)
    private String capacity;

    @Column(name = "base_price")
    private Integer basePrice;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public MovingService(String id, ServiceProvider provider, MovingType movingType,
                         String capacity, Integer basePrice, String additionalInfo) {
        this.id = id;
        this.provider = provider;
        this.movingType = movingType;
        this.capacity = capacity;
        this.basePrice = basePrice;
        this.additionalInfo = additionalInfo;
        this.createdAt = LocalDateTime.now();
    }
}