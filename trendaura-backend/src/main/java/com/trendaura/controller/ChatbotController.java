package com.trendaura.controller;

import com.trendaura.dto.ChatMessageRequest;
import com.trendaura.dto.ChatMessageResponse;
import com.trendaura.entity.ChatMessage;
import com.trendaura.entity.ChatSession;
import com.trendaura.entity.User;
import com.trendaura.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatbotController {
    
    private final ChatbotService chatbotService;
    
    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal User user) {
        
        ChatMessageResponse response = chatbotService.processMessage(request, user);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/message-anonymous")
    public ResponseEntity<ChatMessageResponse> sendAnonymousMessage(
            @Valid @RequestBody ChatMessageRequest request) {
        
        ChatMessageResponse response = chatbotService.processMessage(request, null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatbotService.getChatHistory(sessionId));
    }
    
    @PostMapping("/session/new")
    public ResponseEntity<ChatSession> createNewSession(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatbotService.createNewSession(user));
    }
    
    @PostMapping("/session/new-anonymous")
    public ResponseEntity<ChatSession> createAnonymousSession() {
        return ResponseEntity.ok(chatbotService.createNewSession(null));
    }
    
    @PostMapping("/session/{sessionId}/end")
    public ResponseEntity<Void> endChatSession(@PathVariable String sessionId) {
        chatbotService.endChatSession(sessionId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/sessions/active")
    public ResponseEntity<List<ChatSession>> getActiveSessions() {
        return ResponseEntity.ok(chatbotService.getActiveSessions());
    }
    
    @GetMapping("/sessions/active-count")
    public ResponseEntity<Long> getActiveSessionCount() {
        return ResponseEntity.ok(chatbotService.getActiveSessionCount());
    }
}
