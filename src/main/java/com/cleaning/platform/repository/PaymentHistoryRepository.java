package com.cleaning.platform.repository;
import com.cleaning.platform.domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {
    List<PaymentHistory> findByBookingUsersEmail(String email);
    Optional<PaymentHistory> findByImpUid(String impUid); // impUid로 결제 내역을 찾는 메소드 추가
}