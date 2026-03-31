package com.alexandrekpr.picpay_simplified.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.NotificationDTO;

public class NotificationService {
  @Autowired
  private RestTemplate restTemplate;

  public void sendNotification(User user, String message) throws Exception {
    String mail = user.getEmail();
    NotificationDTO notificationRequest = new NotificationDTO(mail, message);

    ResponseEntity<String> response = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      System.out.println("Error occurred while sending notification to " + mail);
      throw new Exception("Failed to send notification: " + response.getStatusCode());
    }
  }
}
