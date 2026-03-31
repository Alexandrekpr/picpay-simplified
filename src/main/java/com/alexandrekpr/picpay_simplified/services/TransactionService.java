package com.alexandrekpr.picpay_simplified.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alexandrekpr.picpay_simplified.domain.transaction.Transaction;
import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.AuthResponse;
import com.alexandrekpr.picpay_simplified.dtos.TransactionDTO;
import com.alexandrekpr.picpay_simplified.repositories.TransactionRepository;

@Service
public class TransactionService {
  @Autowired
  private UserService userService;

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private RestTemplate restTemplate;

  public Transaction createTransaction(TransactionDTO transaction) throws Exception {
    User sender = userService.findById(transaction.senderId());
    User receiver = userService.findById(transaction.receiverId());
    
    userService.validateTransaction(sender, transaction.value());
    
    boolean isAuthenticated = authenticateTransaction(sender, transaction.value());
    if (!isAuthenticated) {
      throw new Exception("Transaction authentication failed");
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

    //TODO: chamar o serviço de notificação para o sender e para o receiveraqui (não implementado)
    return newTransaction;
}

public boolean authenticateTransaction(User sender, BigDecimal amount) {
    try {
      ResponseEntity<AuthResponse> response = restTemplate.getForEntity(
            "https://util.devi.tools/api/v2/authorize", 
            AuthResponse.class
    );

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      AuthResponse body = response.getBody();
      return "success".equals(body.status()) && body.data().authorization();
    }
    } catch (Exception e) {
      return false;
    }

    return false;
  }
}
