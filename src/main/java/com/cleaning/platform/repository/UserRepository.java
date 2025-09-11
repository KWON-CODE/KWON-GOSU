package com.cleaning.platform.repository;

import com.cleaning.platform.domain.Users;
import com.cleaning.platform.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByVerificationToken(String token);

    Optional<Users> findByUsername(String username);
}