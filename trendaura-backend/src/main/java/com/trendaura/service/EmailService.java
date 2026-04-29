package com.trendaura.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    /**
     * Sends a plain text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@trendaura.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Sends an HTML email
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("noreply@trendaura.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Error sending HTML email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
    
    /**
     * Sends login notification email
     */
    public void sendLoginNotification(String email, String firstName, String ipAddress, String userAgent) {
        String subject = "New Login to TrendAura Account";
        
        String htmlContent = String.format(
            "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                        "<h2 style='color: #e83e8c;'>Welcome Back, %s!</h2>" +
                        "<p>We noticed a new login to your TrendAura account.</p>" +
                        "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin: 20px 0;'>" +
                            "<p><strong>Login Details:</strong></p>" +
                            "<p><strong>Date & Time:</strong> %s</p>" +
                            "<p><strong>IP Address:</strong> %s</p>" +
                            "<p><strong>Device/Browser:</strong> %s</p>" +
                        "</div>" +
                        "<p style='color: #666; font-size: 14px;'>If this wasn't you, please secure your account by changing your password immediately.</p>" +
                        "<p style='color: #666; font-size: 14px;'><strong>Contact us at support@trendaura.com if you have any concerns.</strong></p>" +
                        "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='color: #999; font-size: 12px;'>© 2026 TrendAura. All rights reserved.</p>" +
                        "<p style='color: #999; font-size: 12px;'>This is an automated message, please do not reply.</p>" +
                    "</div>" +
                "</body>" +
            "</html>",
            firstName,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")),
            ipAddress != null ? ipAddress : "Unknown",
            userAgent != null ? userAgent.substring(0, Math.min(100, userAgent.length())) : "Unknown"
        );
        
        sendHtmlEmail(email, subject, htmlContent);
    }
    
    /**
     * Sends registration welcome email
     */
    public void sendWelcomeEmail(String email, String firstName, String lastName) {
        String subject = "Welcome to TrendAura - Your Fashion Destination!";
        
        String htmlContent = String.format(
            "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                        "<h2 style='color: #e83e8c;'>Welcome to TrendAura, %s!</h2>" +
                        "<p>Thank you for joining our fashion community. We're thrilled to have you on board!</p>" +
                        "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin: 20px 0;'>" +
                            "<p><strong>You Can Now:</strong></p>" +
                            "<ul>" +
                                "<li>Browse our exclusive fashion collections</li>" +
                                "<li>Save items to your wishlist</li>" +
                                "<li>Enjoy exclusive member discounts</li>" +
                                "<li>Track your orders in real-time</li>" +
                                "<li>Get personalized recommendations</li>" +
                            "</ul>" +
                        "</div>" +
                        "<p style='text-align: center; padding: 20px;'>" +
                            "<a href='https://trendaura.com' style='display: inline-block; background-color: #e83e8c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; font-weight: bold;'>Start Shopping Now</a>" +
                        "</p>" +
                        "<p style='color: #666; font-size: 14px;'>Have questions? Contact our support team at support@trendaura.com</p>" +
                        "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='color: #999; font-size: 12px;'>© 2026 TrendAura. All rights reserved.</p>" +
                    "</div>" +
                "</body>" +
            "</html>",
            firstName
        );
        
        sendHtmlEmail(email, subject, htmlContent);
    }
    
    /**
     * Sends contact message confirmation email
     */
    public void sendContactConfirmationEmail(String email, String name) {
        String subject = "We Received Your Message - TrendAura Support";
        
        String htmlContent = String.format(
            "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                        "<h2 style='color: #e83e8c;'>Thank You, %s!</h2>" +
                        "<p>We have received your message and our support team will review it shortly.</p>" +
                        "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin: 20px 0;'>" +
                            "<p><strong>What's Next?</strong></p>" +
                            "<p>Our customer support team will respond to your inquiry within 24 business hours.</p>" +
                            "<p>You will receive a follow-up email with our response.</p>" +
                        "</div>" +
                        "<p style='color: #666; font-size: 14px;'>If you have an urgent matter, please call us at +91 8520096953 (Mon-Sat, 10AM-7PM)</p>" +
                        "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='color: #999; font-size: 12px;'>© 2026 TrendAura. All rights reserved.</p>" +
                    "</div>" +
                "</body>" +
            "</html>",
            name
        );
        
        sendHtmlEmail(email, subject, htmlContent);
    }
    
    /**
     * Sends password reset email
     */
    public void sendPasswordResetEmail(String email, String resetToken) {
        String subject = "Reset Your TrendAura Password";
        
        String resetLink = String.format("https://trendaura.com/reset-password?token=%s", resetToken);
        
        String htmlContent = String.format(
            "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                        "<h2 style='color: #e83e8c;'>Password Reset Request</h2>" +
                        "<p>We received a request to reset your password. Click the button below to proceed.</p>" +
                        "<p style='text-align: center; padding: 20px;'>" +
                            "<a href='%s' style='display: inline-block; background-color: #e83e8c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; font-weight: bold;'>Reset Password</a>" +
                        "</p>" +
                        "<p style='color: #666; font-size: 14px;'>This link will expire in 24 hours.</p>" +
                        "<p style='color: #999; font-size: 12px;'>If you didn't request this, please ignore this email.</p>" +
                        "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='color: #999; font-size: 12px;'>© 2026 TrendAura. All rights reserved.</p>" +
                    "</div>" +
                "</body>" +
            "</html>",
            resetLink
        );
        
        sendHtmlEmail(email, subject, htmlContent);
    }
}
