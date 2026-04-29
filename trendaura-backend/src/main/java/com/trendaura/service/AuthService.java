package com.trendaura.service;

import com.trendaura.dto.*;
import com.trendaura.entity.User;
import com.trendaura.repository.UserRepository;
import com.trendaura.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, 
                       @Autowired(required = false) EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        // Send login notification email asynchronously
        if (emailService != null) {
            try {
                String ipAddress = getClientIp();
                String userAgent = getUserAgent();
                emailService.sendLoginNotification(
                    user.getEmail(),
                    user.getFirstName(),
                    ipAddress,
                    userAgent
                );
                log.info("Login notification email sent to: {}", user.getEmail());
            } catch (Exception e) {
                log.warn("Failed to send login notification email: {}", e.getMessage());
                // Don't fail the login if email service is unavailable
            }
        }
        
        return new AuthResponse(token, toUserResponse(user));
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setCountryCode(req.getCountryCode());
        user.setSeller(req.isSeller());
        user.setSellerDescription(req.getSellerDescription());
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        // Send welcome email asynchronously
        if (emailService != null) {
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), user.getLastName());
                log.info("Welcome email sent to: {}", user.getEmail());
            } catch (Exception e) {
                log.warn("Failed to send welcome email: {}", e.getMessage());
                // Don't fail the registration if email service is unavailable
            }
        }
        
        return new AuthResponse(token, toUserResponse(user));
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && emailService != null) {
            try {
                String resetToken = jwtUtil.generateToken(user.getId(), user.getEmail());
                emailService.sendPasswordResetEmail(email, resetToken);
                log.info("Password reset email sent to: {}", email);
            } catch (Exception e) {
                log.warn("Failed to send password reset email: {}", e.getMessage());
            }
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIp() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }
            return clientIp;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Get user agent from request
     */
    private String getUserAgent() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return request.getHeader("User-Agent");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setFullName(user.getFullName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setCountryCode(user.getCountryCode());
        r.setSeller(user.isSeller());
        r.setSellerDescription(user.getSellerDescription());
        return r;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserFromToken(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            return getUserById(userId);
        } catch (Exception e) {
            return null;
        }
    }
}
