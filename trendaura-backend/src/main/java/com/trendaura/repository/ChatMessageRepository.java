package com.trendaura.repository;

import com.trendaura.entity.ChatMessage;
import com.trendaura.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByChatSessionOrderByTimestampAsc(ChatSession chatSession);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.sessionId = :sessionId ORDER BY cm.timestamp ASC")
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(@Param("sessionId") String sessionId);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatSession.sessionId = :sessionId")
    Long countBySessionId(@Param("sessionId") String sessionId);
}
