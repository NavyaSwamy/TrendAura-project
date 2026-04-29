package com.trendaura.controller;

import com.trendaura.dto.ContactMessageRequest;
import com.trendaura.entity.ContactMessage;
import com.trendaura.entity.User;
import com.trendaura.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ContactController {
    
    private final ContactService contactService;
    
    @PostMapping("/send")
    public ResponseEntity<ContactMessage> sendContactMessage(
            @Valid @RequestBody ContactMessageRequest request,
            @AuthenticationPrincipal User user) {
        
        ContactMessage savedMessage = contactService.saveContactMessage(request, user);
        return ResponseEntity.ok(savedMessage);
    }
    
    @PostMapping("/send-anonymous")
    public ResponseEntity<ContactMessage> sendAnonymousContactMessage(
            @Valid @RequestBody ContactMessageRequest request) {
        
        ContactMessage savedMessage = contactService.saveContactMessage(request, null);
        return ResponseEntity.ok(savedMessage);
    }
    
    @GetMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }
    
    @GetMapping("/messages/unresolved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getUnresolvedMessages() {
        return ResponseEntity.ok(contactService.getUnresolvedMessages());
    }
    
    @GetMapping("/messages/resolved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getResolvedMessages() {
        return ResponseEntity.ok(contactService.getResolvedMessages());
    }
    
    @GetMapping("/messages/user")
    public ResponseEntity<List<ContactMessage>> getUserMessages(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contactService.getUserMessages(user));
    }
    
    @GetMapping("/messages/unresolved-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getUnresolvedMessageCount() {
        return ResponseEntity.ok(contactService.getUnresolvedMessageCount());
    }
    
    @PutMapping("/messages/{messageId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessage> markAsResolved(@PathVariable Long messageId) {
        return ResponseEntity.ok(contactService.markAsResolved(messageId));
    }
    
    @GetMapping("/messages/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getMessagesByEmail(@PathVariable String email) {
        return ResponseEntity.ok(contactService.getMessagesByEmail(email));
    }
}
