package com.trendaura.controller;

import com.trendaura.dto.ActivityLogRequest;
import com.trendaura.entity.User;
import com.trendaura.service.ActivityTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ActivityTrackingController {
    
    private final ActivityTrackingService activityTrackingService;
    
    @PostMapping("/log")
    public ResponseEntity<String> logActivity(
            @Valid @RequestBody ActivityLogRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        
        // Extract IP address and user agent
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        // Set device info and IP address
        request.setIpAddress(ipAddress);
        request.setDeviceInfo(userAgent);
        
        if (user != null) {
            activityTrackingService.logActivity(request, user);
        } else {
            activityTrackingService.logActivityForAnonymousUser(request);
        }
        
        return ResponseEntity.ok("Activity logged successfully");
    }
    
    @PostMapping("/log-anonymous")
    public ResponseEntity<String> logAnonymousActivity(
            @Valid @RequestBody ActivityLogRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        request.setIpAddress(ipAddress);
        request.setDeviceInfo(userAgent);
        
        activityTrackingService.logActivityForAnonymousUser(request);
        
        return ResponseEntity.ok("Anonymous activity logged successfully");
    }
    
    @GetMapping("/user-history")
    public ResponseEntity<?> getUserActivityHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(activityTrackingService.getUserActivityHistory(user));
    }
    
    @GetMapping("/analytics/most-visited-pages")
    public ResponseEntity<?> getMostVisitedPages(
            @RequestParam(defaultValue = "7") int days) {
        
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(activityTrackingService.getMostVisitedPages(since));
    }
    
    @GetMapping("/analytics/most-common-actions")
    public ResponseEntity<?> getMostCommonActions(
            @RequestParam(defaultValue = "7") int days) {
        
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(activityTrackingService.getMostCommonActions(since));
    }
    
    @GetMapping("/analytics/page-visits")
    public ResponseEntity<Long> getPageVisitCount(
            @RequestParam String pageName,
            @RequestParam(defaultValue = "7") int days) {
        
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(activityTrackingService.getPageVisitCount(pageName, since));
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
