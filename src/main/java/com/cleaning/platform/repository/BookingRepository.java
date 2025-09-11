package com.cleaning.platform.repository;
import com.cleaning.platform.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByUsersEmailOrderByCreatedAtDesc(String email);

    List<Booking> findByPostIdOrderByCreatedAtDesc(String postId);

    List<Booking> findByProviderIdOrderByCreatedAtDesc(String providerId);

    boolean existsByUsersIdAndPostId(String userId, String postId);

}