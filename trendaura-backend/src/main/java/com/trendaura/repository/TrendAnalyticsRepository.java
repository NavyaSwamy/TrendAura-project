package com.trendaura.repository;

import com.trendaura.entity.TrendAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendAnalyticsRepository extends JpaRepository<TrendAnalytics, Long> {

    List<TrendAnalytics> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
}
