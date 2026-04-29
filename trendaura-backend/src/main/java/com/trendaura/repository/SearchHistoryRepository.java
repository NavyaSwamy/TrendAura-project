package com.trendaura.repository;

import com.trendaura.entity.SearchHistory;
import com.trendaura.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    
    List<SearchHistory> findByUserOrderByTimestampDesc(User user);
    
    List<SearchHistory> findBySearchQueryContainingIgnoreCaseOrderByTimestampDesc(String query);
    
    @Query("SELECT sh FROM SearchHistory sh WHERE sh.user = :user AND sh.timestamp >= :since ORDER BY sh.timestamp DESC")
    List<SearchHistory> findByUserAndTimestampAfterOrderByTimestampDesc(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT sh.searchQuery, COUNT(sh) FROM SearchHistory sh WHERE sh.timestamp >= :since GROUP BY sh.searchQuery ORDER BY COUNT(sh) DESC")
    List<Object[]> findPopularSearchQueriesSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(sh) FROM SearchHistory sh WHERE sh.timestamp >= :since")
    Long countSearchesSince(@Param("since") LocalDateTime since);
}
