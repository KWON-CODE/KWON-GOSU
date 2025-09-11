package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MESSAGES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @Column(name = "message_id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_booking_id")
    private Booking relatedBooking;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Users receiver;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;


    @Builder
    public Message(String id, Booking relatedBooking, Users sender, Users receiver, String content) {
        this.id = id;
        this.relatedBooking = relatedBooking;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }
}