package com.alexandrekpr.picpay_simplified.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.NotificationDTO;

@Service
public class NotificationService {
  @Autowired
  private RestTemplate restTemplate;

  public void sendNotification(User user, String message) throws Exception {
    String mail = user.getEmail();
    NotificationDTO notificationRequest = new NotificationDTO(mail, message);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);
      System.err.println("Notification successfully sent. response: " + response.getBody());
    } catch (Exception e) {
      System.err.println("Error occurred while sending notification to " + mail + ": " + e.getMessage());
    }
  }
}
