package com.notification.web;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import com.notification.service.NotificationService;
import com.notification.web.dto.NotificationRequest;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private NotificationService notificationService;
    
    @Test
    void testSendNotification() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setRecipient("user@example.com");
        request.setSubject("Test Subject");
        request.setContent("Test Content");
        request.setChannel(DeliveryChannel.EMAIL);
        
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setRecipient(request.getRecipient());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setChannel(request.getChannel());
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        when(notificationService.sendNotificationImmediately(any(Notification.class))).thenReturn(notification);
        
        // When/Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.recipient", is(request.getRecipient())))
                .andExpect(jsonPath("$.subject", is(request.getSubject())))
                .andExpect(jsonPath("$.content", is(request.getContent())))
                .andExpect(jsonPath("$.status", is(notification.getStatus().name())));
    }
    
    @Test
    void testGetNotificationsByRecipient() throws Exception {
        // Given
        String recipient = "user@example.com";
        
        Notification notification1 = new Notification();
        notification1.setId(UUID.randomUUID());
        notification1.setRecipient(recipient);
        notification1.setSubject("Test Subject 1");
        
        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());
        notification2.setRecipient(recipient);
        notification2.setSubject("Test Subject 2");
        
        Page<Notification> page = new PageImpl<>(Arrays.asList(notification1, notification2));
        
        when(notificationService.getNotificationsByRecipient(eq(recipient), any(Pageable.class))).thenReturn(page);
        
        // When/Then
        mockMvc.perform(get("/api/notifications/recipient/{recipient}", recipient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].recipient", is(recipient)))
                .andExpect(jsonPath("$.content[1].recipient", is(recipient)));
    }
    
    @Test
    void testDeleteNotification() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        
        doNothing().when(notificationService).deleteNotification(id);
        
        // When/Then
        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isOk());
        
        verify(notificationService).deleteNotification(id);
    }
} 