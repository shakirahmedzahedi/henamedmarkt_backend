package com.saz.se.goat.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByClientIdOrderByTimestampAsc(String clientId);

    @Query("SELECT c.clientId FROM ChatMessage c GROUP BY c.clientId ORDER BY MAX(c.timestamp) DESC")
    List<String> findAllDistinctClientIdsOrderByLatestMessage();
}