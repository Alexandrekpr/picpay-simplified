package com.alexandrekpr.picpay_simplified.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alexandrekpr.picpay_simplified.domain.transaction.Transaction;
import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.AuthResponse;
import com.alexandrekpr.picpay_simplified.dtos.TransactionDTO;
import com.alexandrekpr.picpay_simplified.exceptions.ExternalApiException;
import com.alexandrekpr.picpay_simplified.exceptions.ForbiddenException;
import com.alexandrekpr.picpay_simplified.repositories.TransactionRepository;

@Service
public class TransactionService {

    private final UserService userService;
    private final NotificationService notificationService;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(
      UserService userService, 
        NotificationService notificationService, 
        TransactionRepository transactionRepository, 
        RestTemplate restTemplate
      ) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

  @Transactional
  public Transaction createTransaction(TransactionDTO transaction) throws Exception {
    User sender = userService.findById(transaction.senderId());
    User receiver = userService.findById(transaction.receiverId());
    
    userService.validateTransaction(sender, transaction.value());
    
    boolean isAuthenticated = authenticateTransaction(sender, transaction.value());
    if (!isAuthenticated) {
      throw new ForbiddenException();
    }

    Transaction newTransaction = new Transaction();
    newTransaction.setSender(sender);
    newTransaction.setReceiver(receiver);
    newTransaction.setAmount(transaction.value());
    newTransaction.setTimestamp(LocalDateTime.now());

    sender.setBalance(sender.getBalance().subtract(transaction.value()));
    receiver.setBalance(receiver.getBalance().add(transaction.value()));

    this.transactionRepository.save(newTransaction);
    this.userService.saveUser(sender);
    this.userService.saveUser(receiver);

    this.notificationService.sendNotification(receiver, "Você recebeu uma transação.");

    return newTransaction;
}

public boolean authenticateTransaction(User sender, BigDecimal amount) throws ExternalApiException {
    ResponseEntity<AuthResponse> response = restTemplate.getForEntity(
          "https://util.devi.tools/api/v2/authorize", 
          AuthResponse.class
    );

    System.err.println("Resposta da API de autenticação: " + response);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      String status = response.getBody().status();
      boolean isAuthorized = response.getBody().data().authorization();
      
      if ("fail".equalsIgnoreCase(status) || !isAuthorized) {
            throw new ForbiddenException();
      }
      
      return true;
    }

    return false;
  }
}
