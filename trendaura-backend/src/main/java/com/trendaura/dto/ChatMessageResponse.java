package com.trendaura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    
    private String sessionId;
    
    private String message;
    
    private String messageType;
    
    private LocalDateTime timestamp;
    
    private boolean isSessionActive;
}
