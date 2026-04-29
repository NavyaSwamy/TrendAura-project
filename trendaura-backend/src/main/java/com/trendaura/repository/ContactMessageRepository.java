package com.trendaura.repository;

import com.trendaura.entity.ContactMessage;
import com.trendaura.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    
    List<ContactMessage> findByUserOrderByTimestampDesc(User user);
    
    List<ContactMessage> findByIsResolvedFalseOrderByTimestampDesc();
    
    List<ContactMessage> findByIsResolvedTrueOrderByTimestampDesc();
    
    @Query("SELECT COUNT(cm) FROM ContactMessage cm WHERE cm.isResolved = false")
    Long countUnresolvedMessages();
    
    @Query("SELECT cm FROM ContactMessage cm WHERE cm.email = :email ORDER BY cm.timestamp DESC")
    List<ContactMessage> findByEmailOrderByTimestampDesc(String email);
}
