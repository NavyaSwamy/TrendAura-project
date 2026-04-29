package com.trendaura.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogRequest {
    
    @NotBlank(message = "Page name is required")
    private String pageName;
    
    @NotBlank(message = "Action name is required")
    private String actionName;
    
    private String deviceInfo;
    
    private String ipAddress;
    
    private String sessionId;
    
    private String additionalData;
}
