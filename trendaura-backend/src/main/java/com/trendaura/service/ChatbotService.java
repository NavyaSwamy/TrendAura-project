package com.trendaura.service;

import com.trendaura.dto.ChatMessageRequest;
import com.trendaura.dto.ChatMessageResponse;
import com.trendaura.entity.ChatMessage;
import com.trendaura.entity.ChatSession;
import com.trendaura.entity.User;
import com.trendaura.repository.ChatMessageRepository;
import com.trendaura.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {
    
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    public ChatMessageResponse processMessage(ChatMessageRequest request, User user) {
        try {
            // Get or create chat session
            ChatSession chatSession = getOrCreateChatSession(request.getSessionId(), user);
            
            // Save user message
            saveChatMessage(chatSession, ChatMessage.MessageType.USER, request.getMessage());
            
            // Generate bot response
            String botResponse = generateBotResponse(request.getMessage());
            
            // Save bot message
            saveChatMessage(chatSession, ChatMessage.MessageType.BOT, botResponse);
            
            return ChatMessageResponse.builder()
                    .sessionId(chatSession.getSessionId())
                    .message(botResponse)
                    .messageType("BOT")
                    .timestamp(LocalDateTime.now())
                    .isSessionActive(chatSession.getIsActive())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing chat message: {}", e.getMessage(), e);
            return ChatMessageResponse.builder()
                    .sessionId(request.getSessionId())
                    .message("I'm sorry, I'm having trouble processing your message right now. Please try again later.")
                    .messageType("BOT")
                    .timestamp(LocalDateTime.now())
                    .isSessionActive(false)
                    .build();
        }
    }
    
    private ChatSession getOrCreateChatSession(String sessionId, User user) {
        if (sessionId != null && !sessionId.isEmpty()) {
            Optional<ChatSession> existingSession = chatSessionRepository.findBySessionId(sessionId);
            if (existingSession.isPresent() && existingSession.get().getIsActive()) {
                return existingSession.get();
            }
        }
        
        // Create new session
        String newSessionId = UUID.randomUUID().toString();
        ChatSession newSession = ChatSession.builder()
                .user(user)
                .sessionId(newSessionId)
                .isActive(true)
                .build();
        
        return chatSessionRepository.save(newSession);
    }
    
    private void saveChatMessage(ChatSession chatSession, ChatMessage.MessageType messageType, String messageText) {
        ChatMessage chatMessage = ChatMessage.builder()
                .chatSession(chatSession)
                .messageType(messageType)
                .messageText(messageText)
                .build();
        
        chatMessageRepository.save(chatMessage);
    }
    
    private String generateBotResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase().trim();
        
        // Greeting responses
        if (lowerMessage.contains("hello") || lowerMessage.contains("hi") || lowerMessage.contains("hey")) {
            return "Hello! Welcome to TrendAura! How can I help you today? I can assist you with product information, orders, shipping details, and more.";
        }
        
        // Product related queries
        if (lowerMessage.contains("product") || lowerMessage.contains("saree") || lowerMessage.contains("dress")) {
            return "I can help you find the perfect product! We have a wide collection of traditional and modern fashion items. You can browse our collections page or search for specific items. What type of product are you looking for?";
        }
        
        // Order related queries
        if (lowerMessage.contains("order") || lowerMessage.contains("shipping") || lowerMessage.contains("delivery")) {
            return "For order inquiries, I can help you track your order, check shipping status, or provide delivery information. Please provide your order number if you have specific questions about your order.";
        }
        
        // Payment related queries
        if (lowerMessage.contains("payment") || lowerMessage.contains("price") || lowerMessage.contains("cost")) {
            return "We accept various payment methods including credit/debit cards, UPI, and cash on delivery. All our products are competitively priced with the best quality assured. Is there a specific product you'd like to know the price of?";
        }
        
        // Return/Exchange queries
        if (lowerMessage.contains("return") || lowerMessage.contains("exchange") || lowerMessage.contains("refund")) {
            return "We have a hassle-free return and exchange policy. You can return or exchange products within 7 days of delivery. The product should be in original condition with tags intact. Would you like to know more about our return policy?";
        }
        
        // Contact/Support queries
        if (lowerMessage.contains("contact") || lowerMessage.contains("support") || lowerMessage.contains("help")) {
            return "You can reach our customer support team through the contact form on our website, or email us at support@trendaura.com. We typically respond within 24 hours. For urgent matters, you can also call us at +91-XXXXXXXXXX.";
        }
        
        // Account related queries
        if (lowerMessage.contains("account") || lowerMessage.contains("login") || lowerMessage.contains("register")) {
            return "You can create an account on TrendAura to track your orders, save your wishlist, and get personalized recommendations. Simply click on the login/register button on our website. Having an account gives you a better shopping experience!";
        }
        
        // Size/Measurement queries
        if (lowerMessage.contains("size") || lowerMessage.contains("measurement") || lowerMessage.contains("fit")) {
            return "We provide detailed size charts for all our products. You can find the size guide on each product page. If you need help with sizing, our customer service team can assist you in finding the perfect fit.";
        }
        
        // Quality/Material queries
        if (lowerMessage.contains("quality") || lowerMessage.contains("material") || lowerMessage.contains("fabric")) {
            return "At TrendAura, we pride ourselves on offering high-quality products made from premium materials. Each product description includes detailed information about the fabric and care instructions. We source our materials from trusted suppliers to ensure the best quality.";
        }
        
        // Default response
        return "Thank you for your message! I'm here to help you with any questions about TrendAura's products, services, or policies. Could you please provide more details about what you'd like to know? I can assist with product information, orders, shipping, returns, and general inquiries.";
    }
    
    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }
    
    public ChatSession createNewSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession newSession = ChatSession.builder()
                .user(user)
                .sessionId(sessionId)
                .isActive(true)
                .build();
        
        return chatSessionRepository.save(newSession);
    }
    
    public void endChatSession(String sessionId) {
        Optional<ChatSession> session = chatSessionRepository.findBySessionId(sessionId);
        if (session.isPresent()) {
            ChatSession chatSession = session.get();
            chatSession.setIsActive(false);
            chatSession.setEndedAt(LocalDateTime.now());
            chatSessionRepository.save(chatSession);
        }
    }
    
    public List<ChatSession> getActiveSessions() {
        return chatSessionRepository.findByIsActiveTrueOrderByStartedAtDesc();
    }
    
    public Long getActiveSessionCount() {
        return chatSessionRepository.countActiveSessions();
    }
}
