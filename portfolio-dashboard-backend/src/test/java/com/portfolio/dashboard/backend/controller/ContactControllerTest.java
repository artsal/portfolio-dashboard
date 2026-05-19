package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.dto.ContactRequest;
import com.portfolio.dashboard.backend.model.ContactMessage;
import com.portfolio.dashboard.backend.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {

    @Mock
    private ContactMessageRepository contactRepo;

    private ContactController contactController;

    @BeforeEach
    void setUp() {
        contactController = new ContactController(contactRepo);
        ReflectionTestUtils.setField(contactController, "recipientEmail", "owner@example.com");
        ReflectionTestUtils.setField(contactController, "resendApiKey", "test-key");
    }

    @Test
    void sendMailRejectsHoneypotSpamAndLogsSpamStatus() {
        ContactRequest request = request();
        request.setWebsite("https://spam.example");
        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);

        ResponseEntity<String> response = contactController.sendMail(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("Spam detected. Message ignored.");
        verify(contactRepo).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Visitor");
        assertThat(captor.getValue().getEmail()).isEqualTo("visitor@example.com");
        assertThat(captor.getValue().getMessage()).isEqualTo("Hello");
        assertThat(captor.getValue().getStatus()).isEqualTo("SPAM");
    }

    @Test
    void sendMailLogsFailedAndReturnsGracefulResponseWhenEmailRequestCannotBeBuilt() {
        ContactRequest request = request();
        ReflectionTestUtils.setField(contactController, "resendApiKey", "bad\nkey");
        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);

        ResponseEntity<String> response = contactController.sendMail(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Message received (email may be delayed)");
        verify(contactRepo).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("FAILED");
    }

    private ContactRequest request() {
        ContactRequest request = new ContactRequest();
        request.setName("Visitor");
        request.setEmail("visitor@example.com");
        request.setMessage("Hello");
        return request;
    }
}
