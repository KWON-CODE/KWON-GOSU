package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory {

    @Id
    @Column(name = "payment_id", length = 255)
    private String id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false)
    private int amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false, length=255)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "imp_uid", nullable = false, unique = true, length = 255)
    private String impUid;


    @Column(name = "merchant_uid", nullable = false, length = 255)
    private String merchantUid;

    @Builder
    public PaymentHistory(String id, Booking booking, int amount, String paymentMethod, String impUid) {
        this.id = id;
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.impUid = impUid;
        this.merchantUid = booking.getId();
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.SUCCESS;
    }
}