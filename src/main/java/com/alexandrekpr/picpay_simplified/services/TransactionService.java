package com.alexandrekpr.picpay_simplified.services;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.TransactionDTO;
import com.alexandrekpr.picpay_simplified.exceptions.ForbiddenException;
import com.alexandrekpr.picpay_simplified.repositories.TransactionRepository;
import com.alexandrekpr.picpay_simplified.domain.transaction.Transaction;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserService userService;
    private final NotificationService notificationService;
    private final TransactionRepository transactionRepository;
    private final AuthenticateService authService;

  @Transactional
  public Transaction createTransaction(TransactionDTO transaction) throws Exception {
    User sender = userService.findById(transaction.senderId());
    User receiver = userService.findById(transaction.receiverId());
    
    userService.validateTransaction(sender, transaction.value());
    
    boolean isAuthenticated = this.authService.authenticateTransaction(sender, transaction.value());
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
}
