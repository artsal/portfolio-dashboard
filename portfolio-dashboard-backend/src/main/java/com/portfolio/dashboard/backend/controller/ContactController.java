package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.dto.ContactRequest;
import com.portfolio.dashboard.backend.model.ContactMessage;
import com.portfolio.dashboard.backend.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin
public class ContactController {

    private final JavaMailSender mailSender;
    private final ContactMessageRepository contactRepo;

    // Inject recipient address from application.properties (your email)
    @Value("${contact.recipient.email}")
    private String recipientEmail;

    public ContactController(JavaMailSender mailSender,
                             ContactMessageRepository contactRepo) {
        this.mailSender = mailSender;
        this.contactRepo = contactRepo;
    }

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody ContactRequest req) {
        ContactMessage log = new ContactMessage();
        log.setName(req.getName());
        log.setEmail(req.getEmail());
        log.setMessage(req.getMessage());
        log.setStatus("PENDING");

        try {
            // 🧩 Honeypot spam check
            if (req.getWebsite() != null && !req.getWebsite().trim().isEmpty()) {
                // “website” is a hidden field; if filled, it’s probably a bot
                log.setStatus("SPAM");
                contactRepo.save(log);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Spam detected. Message ignored.");
            }

            // 📨 1. Email to portfolio owner (you)
            SimpleMailMessage ownerMessage = new SimpleMailMessage();
            ownerMessage.setTo(recipientEmail);
            ownerMessage.setSubject("📬 New message from " + req.getName());
            ownerMessage.setText(
                    "You’ve received a new message from your portfolio site:\n\n" +
                            "--------------------------------------------\n" +
                            "Name: " + req.getName() + "\n" +
                            "Email: " + req.getEmail() + "\n\n" +
                            req.getMessage() + "\n" +
                            "--------------------------------------------\n\n" +
                            "Sent via Arthur’s Portfolio Dashboard."
                                );
            ownerMessage.setFrom("Arthur’s Portfolio Contact Form <" + recipientEmail + ">");
            mailSender.send(ownerMessage);

            // 💌 2. Auto-reply to visitor
            SimpleMailMessage reply = new SimpleMailMessage();
            reply.setTo(req.getEmail());
            reply.setSubject("Thanks for contacting Arthur Salla");
            reply.setText(
                    "Hi " + req.getName() + ",\n\n" +
                            "Thank you for reaching out through my portfolio dashboard! 🙏\n" +
                            "I’ve received your message and will get back to you as soon as possible.\n\n" +
                            "Best regards,\n" +
                            "Arthur\n" +
                            "— Portfolio Dashboard\n\n" +
                            "📫 This is an automated confirmation."
                         );
            reply.setFrom("Arthur’s Portfolio Contact Form <" + recipientEmail + ">");
            mailSender.send(reply);

            log.setStatus("SENT");
            contactRepo.save(log);
            return ResponseEntity.ok("Message sent successfully");

        } catch (Exception e) {
            log.setStatus("FAILED");
            contactRepo.save(log);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending message: " + e.getMessage());
        }
    }
}