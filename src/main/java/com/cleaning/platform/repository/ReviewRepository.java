package com.cleaning.platform.repository;
import com.cleaning.platform.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReviewRepository extends JpaRepository<Review, String> {}