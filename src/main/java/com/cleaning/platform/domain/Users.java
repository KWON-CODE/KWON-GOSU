package com.cleaning.platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER1234")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {


    @Id
    @Column(name = "user_id", length = 50)
    private String id;


    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    //    @Column(length = 255)
    //    private String address;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.GENERAL;



    @Enumerated(EnumType.STRING)
    private SocialProvider provider;


    @OneToOne(mappedBy = "users", fetch = FetchType.LAZY)
    @ToString.Exclude
    private ServiceProvider serviceProvider;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(unique = true)
    private String verificationToken;


    @Builder
    public Users(String id, String username,
                 String password, String email,
                 String phoneNumber, Address address,
                 UserType userType,
                 SocialProvider provider,
                 boolean enabled, String verificationToken) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.provider = provider;
        this.address = address;
        this.userType = userType;
        this.enabled = enabled;
        this.verificationToken = verificationToken;
        this.createdAt = LocalDateTime.now();
    }


    public Users update(String username) {
        this.username = username;
        return this;
    }



    public void completeRegistration(String phoneNumber, Address address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }



    public void update(String username, String phoneNumber, Address address) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}