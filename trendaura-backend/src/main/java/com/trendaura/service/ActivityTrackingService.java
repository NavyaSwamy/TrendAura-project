package com.trendaura.service;

import com.trendaura.dto.ActivityLogRequest;
import com.trendaura.entity.User;
import com.trendaura.entity.UserActivityLog;
import com.trendaura.repository.UserActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityTrackingService {
    
    private final UserActivityLogRepository activityLogRepository;
    
    public void logActivity(ActivityLogRequest request, User user) {
        try {
            UserActivityLog activityLog = UserActivityLog.builder()
                    .user(user)
                    .pageName(request.getPageName())
                    .actionName(request.getActionName())
                    .deviceInfo(request.getDeviceInfo())
                    .ipAddress(request.getIpAddress())
                    .sessionId(request.getSessionId())
                    .additionalData(request.getAdditionalData())
                    .build();
            
            activityLogRepository.save(activityLog);
            log.info("Activity logged: {} - {} for user: {}", 
                    request.getPageName(), request.getActionName(), 
                    user != null ? user.getEmail() : "Anonymous");
                    
        } catch (Exception e) {
            log.error("Error logging activity: {}", e.getMessage(), e);
        }
    }
    
    public void logActivityForAnonymousUser(ActivityLogRequest request) {
        logActivity(request, null);
    }
    
    public List<UserActivityLog> getUserActivityHistory(User user) {
        return activityLogRepository.findByUserOrderByEventTimeDesc(user);
    }
    
    public List<UserActivityLog> getUserActivitySince(User user, LocalDateTime since) {
        return activityLogRepository.findByUserAndEventTimeAfterOrderByEventTimeDesc(user, since);
    }
    
    public List<Object[]> getMostVisitedPages(LocalDateTime since) {
        return activityLogRepository.findMostVisitedPagesSince(since);
    }
    
    public List<Object[]> getMostCommonActions(LocalDateTime since) {
        return activityLogRepository.findMostCommonActionsSince(since);
    }
    
    public Long getPageVisitCount(String pageName, LocalDateTime since) {
        return activityLogRepository.countByPageNameAndEventTimeAfter(pageName, since);
    }
}
