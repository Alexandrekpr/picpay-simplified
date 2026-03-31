package com.alexandrekpr.picpay_simplified.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexandrekpr.picpay_simplified.domain.transaction.Transaction;
import com.alexandrekpr.picpay_simplified.dtos.TransactionDTO;
import com.alexandrekpr.picpay_simplified.services.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @PostMapping
  public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transaction) throws Exception {
    Transaction createdTransaction = this.transactionService.createTransaction(transaction);
    return ResponseEntity.ok(createdTransaction);
  }
}
