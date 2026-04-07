package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.dto.ContactRequest;
import com.portfolio.dashboard.backend.model.ContactMessage;
import com.portfolio.dashboard.backend.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin
public class ContactController {

    private final ContactMessageRepository contactRepo;

    @Value("${contact.recipient.email}")
    private String recipientEmail;

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    public ContactController(ContactMessageRepository contactRepo) {
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
                log.setStatus("SPAM");
                contactRepo.save(log);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Spam detected. Message ignored.");
            }

            HttpClient client = HttpClient.newHttpClient();

            // 📨 1. Email to YOU (portfolio owner)
            String ownerJson = "{"
                    + "\"from\": \"Portfolio Dashboard <onboarding@resend.dev>\","
                    + "\"to\": [\"" + recipientEmail + "\"],"
                    + "\"subject\": \"New message from " + req.getName() + "\","
                    + "\"text\": \"You’ve received a new message from your portfolio site:\\n\\n"
                    + "--------------------------------------------\\n"
                    + "Name: " + req.getName() + "\\n"
                    + "Email: " + req.getEmail() + "\\n\\n"
                    + req.getMessage().replace("\"", "\\\"") + "\\n"
                    + "--------------------------------------------\\n\\n"
                    + "Sent via Arthur’s Portfolio Dashboard.\""
                    + "}";

            HttpRequest ownerRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(ownerJson))
                    .build();

            client.send(ownerRequest, HttpResponse.BodyHandlers.ofString());

            // 💌 2. Auto-reply to visitor
            String replyJson = "{"
                    + "\"from\": \"Arthur's Portfolio Dashboard <onboarding@resend.dev>\","
                    + "\"to\": [\"" + req.getEmail() + "\"],"
                    + "\"subject\": \"Thanks for contacting Arthur Salla\","
                    + "\"text\": \"Hi " + req.getName() + ",\\n\\n"
                    + "Thank you for reaching out! I’ve received your message and will respond soon.\\n\\n"
                    + "Best regards,\\nArthur\""
                    + "}";

            HttpRequest replyRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(replyJson))
                    .build();

            client.send(replyRequest, HttpResponse.BodyHandlers.ofString());

            // ✅ Success
            log.setStatus("SENT");
            contactRepo.save(log);

            return ResponseEntity.ok("Message sent successfully");

        } catch (Exception e) {
            // ⚠️ Fail gracefully (do NOT break UX)
            log.setStatus("FAILED");
            contactRepo.save(log);

            System.out.println("Resend error: " + e.getMessage());

            return ResponseEntity.ok("Message received (email may be delayed)");
        }
    }
}