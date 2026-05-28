package com.alexandrekpr.picpay_simplified.services;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;
import com.alexandrekpr.picpay_simplified.dtos.TransactionDTO;
import com.alexandrekpr.picpay_simplified.exceptions.ForbiddenException;
import com.alexandrekpr.picpay_simplified.exceptions.InsufficientFundsException;
import com.alexandrekpr.picpay_simplified.repositories.TransactionRepository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticateService authService;

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should create the transaction successfully")
    void createTransactionCase1() throws Exception {
        User sender = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Fernando", "99999999902", "fernando@email.com", "password", new BigDecimal(10), UserType.COMMON);

        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findById(2L)).thenReturn(receiver);

        when(authService.authenticateTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(request);

        verify(transactionRepository, times(1)).save((any()));

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);
    }

    @Test
    @DisplayName("should throw Exception when the transaction is not allowed")
    void createTransactionCase2() throws Exception {
        User sender = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Fernando", "99999999902", "fernando@email.com", "password", new BigDecimal(10), UserType.COMMON);

        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findById(2L)).thenReturn(receiver);

        when(authService.authenticateTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(ForbiddenException.class, () -> {
                TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
                transactionService.createTransaction(request);
        });

        verify(transactionRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw Exception when the sender dont have the balance to complete the transaction")
    void createTransactionCase3() throws Exception {
        User sender = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(0), UserType.COMMON);
        User receiver = new User(2L, "Fernando", "99999999902", "fernando@email.com", "password", new BigDecimal(10), UserType.COMMON);

        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findById(2L)).thenReturn(receiver);

        doThrow(new InsufficientFundsException("Insufficient funds.")).when(userService).validateTransaction(any(), any());

        when(authService.authenticateTransaction(any(), any())).thenReturn(true);

        Exception thrown = Assertions.assertThrows(InsufficientFundsException.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(15), 1L, 2L);
            transactionService.createTransaction(request);
        });

        verify(transactionRepository, times(0)).save(any());
        Assertions.assertEquals("Insufficient funds.", thrown.getMessage());
    }

    @Test
    @DisplayName("should throw Exception when the sender type is MERCHANT")
    void createTransactionCase4() throws Exception {
        User sender = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(15), UserType.MERCHANT);
        User receiver = new User(2L, "Fernando", "99999999902", "fernando@email.com", "password", new BigDecimal(10), UserType.COMMON);

        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findById(2L)).thenReturn(receiver);

        doThrow(new ForbiddenException()).when(userService).validateTransaction(any(), any());

        when(authService.authenticateTransaction(any(), any())).thenReturn(true);

        Exception thrown = Assertions.assertThrows(ForbiddenException.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(15), 1L, 2L);
            transactionService.createTransaction(request);
        });

        verify(transactionRepository, times(0)).save(any());
    }
}