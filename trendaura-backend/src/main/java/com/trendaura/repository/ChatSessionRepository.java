package com.trendaura.repository;

import com.trendaura.entity.ChatSession;
import com.trendaura.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    
    Optional<ChatSession> findBySessionId(String sessionId);
    
    List<ChatSession> findByUserOrderByStartedAtDesc(User user);
    
    List<ChatSession> findByIsActiveTrueOrderByStartedAtDesc();
    
    @Query("SELECT cs FROM ChatSession cs WHERE cs.isActive = true AND cs.startedAt < :cutoffTime")
    List<ChatSession> findInactiveSessionsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(cs) FROM ChatSession cs WHERE cs.isActive = true")
    Long countActiveSessions();
}
