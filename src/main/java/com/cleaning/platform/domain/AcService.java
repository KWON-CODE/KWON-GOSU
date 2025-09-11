package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AC_SERVICES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcService {

    @Id
    @Column(name = "service_id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private AcServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type") // NULL 허용
    private AcProductType productType;

    @Column(name = "price_range_min")
    private Integer priceRangeMin;

    @Column(name = "price_range_max")
    private Integer priceRangeMax;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public AcService(String id, ServiceProvider provider, AcServiceType serviceType, AcProductType productType,
                     Integer priceRangeMin, Integer priceRangeMax, String description) {
        this.id = id;
        this.provider = provider;
        this.serviceType = serviceType;
        this.productType = productType;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}