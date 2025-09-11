package com.cleaning.platform.repository;

import com.cleaning.platform.domain.Message;
import com.cleaning.platform.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findBySenderOrReceiverOrderBySentAtDesc(Users sender, Users receiver);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findChatHistory(@Param("user1") Users user1, @Param("user2") Users user2);

    long countByReceiverAndIsRead(Users receiver, boolean isRead);
}