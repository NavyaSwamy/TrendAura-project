package com.trendaura.service;

import com.trendaura.dto.ContactMessageRequest;
import com.trendaura.entity.ContactMessage;
import com.trendaura.entity.User;
import com.trendaura.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {
    
    private final ContactMessageRepository contactMessageRepository;
    
    @Autowired(required = false)
    private EmailService emailService;
    
    public ContactMessage saveContactMessage(ContactMessageRequest request, User user) {
        try {
            ContactMessage contactMessage = ContactMessage.builder()
                    .user(user)
                    .name(request.getName())
                    .email(request.getEmail())
                    .subject(request.getSubject())
                    .message(request.getMessage())
                    .build();
            
            ContactMessage saved = contactMessageRepository.save(contactMessage);
            log.info("Contact message saved from: {}", request.getEmail());
            
            // Send confirmation email
            if (emailService != null) {
                try {
                    emailService.sendContactConfirmationEmail(request.getEmail(), request.getName());
                    log.info("Contact confirmation email sent to: {}", request.getEmail());
                } catch (Exception e) {
                    log.warn("Failed to send contact confirmation email: {}", e.getMessage());
                }
            }
            
            return saved;
            
        } catch (Exception e) {
            log.error("Error saving contact message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save contact message");
        }
    }
    
    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }
    
    public List<ContactMessage> getUnresolvedMessages() {
        return contactMessageRepository.findByIsResolvedFalseOrderByTimestampDesc();
    }
    
    public List<ContactMessage> getResolvedMessages() {
        return contactMessageRepository.findByIsResolvedTrueOrderByTimestampDesc();
    }
    
    public List<ContactMessage> getUserMessages(User user) {
        return contactMessageRepository.findByUserOrderByTimestampDesc(user);
    }
    
    public Long getUnresolvedMessageCount() {
        return contactMessageRepository.countUnresolvedMessages();
    }
    
    public ContactMessage markAsResolved(Long messageId) {
        ContactMessage message = contactMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.setIsResolved(true);
        return contactMessageRepository.save(message);
    }
    
    public List<ContactMessage> getMessagesByEmail(String email) {
        return contactMessageRepository.findByEmailOrderByTimestampDesc(email);
    }
}
