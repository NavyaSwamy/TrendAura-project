package com.trendaura.repository;

import com.trendaura.entity.User;
import com.trendaura.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    
    List<UserActivityLog> findByUserOrderByEventTimeDesc(User user);
    
    List<UserActivityLog> findByPageNameOrderByEventTimeDesc(String pageName);
    
    @Query("SELECT ual FROM UserActivityLog ual WHERE ual.user = :user AND ual.eventTime >= :since ORDER BY ual.eventTime DESC")
    List<UserActivityLog> findByUserAndEventTimeAfterOrderByEventTimeDesc(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(ual) FROM UserActivityLog ual WHERE ual.pageName = :pageName AND ual.eventTime >= :since")
    Long countByPageNameAndEventTimeAfter(@Param("pageName") String pageName, @Param("since") LocalDateTime since);
    
    @Query("SELECT ual.pageName, COUNT(ual) FROM UserActivityLog ual WHERE ual.eventTime >= :since GROUP BY ual.pageName ORDER BY COUNT(ual) DESC")
    List<Object[]> findMostVisitedPagesSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT ual.actionName, COUNT(ual) FROM UserActivityLog ual WHERE ual.eventTime >= :since GROUP BY ual.actionName ORDER BY COUNT(ual) DESC")
    List<Object[]> findMostCommonActionsSince(@Param("since") LocalDateTime since);
}
