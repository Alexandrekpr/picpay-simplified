package com.alexandrekpr.picpay_simplified.services;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;
import com.alexandrekpr.picpay_simplified.dtos.Exception;
import com.alexandrekpr.picpay_simplified.exceptions.ForbiddenException;
import com.alexandrekpr.picpay_simplified.exceptions.InsufficientFundsException;
import com.alexandrekpr.picpay_simplified.repositories.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should pass the validate without throw")
    void validateTransactionCase1(){
        BigDecimal amount = new BigDecimal(10);
        User user = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(10), UserType.COMMON);
        Assertions.assertDoesNotThrow(() -> {
            userService.validateTransaction(user, amount);
        });
    }

    @Test
    @DisplayName("should throw an exception when the sender is a MERCHANT type")
    void validateTransactionCase2(){
        BigDecimal amount = new BigDecimal(10);
        User user = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(10), UserType.MERCHANT);
        Assertions.assertThrows(ForbiddenException.class, () -> {
            userService.validateTransaction(user, amount);
        });
    }

    @Test
    @DisplayName("should throw an exception when the sender doesnt have the amount to complete the transaction")
    void validateTransactionCase3(){
        BigDecimal amount = new BigDecimal(100);
        User user = new User(1L, "Alexandre", "99999999901", "alexandre@email.com", "password", new BigDecimal(10), UserType.COMMON);
        InsufficientFundsException thrown = Assertions.assertThrows(InsufficientFundsException.class, () -> {
            userService.validateTransaction(user, amount);
        });

        Assertions.assertEquals("Insufficient funds.", thrown.getMessage());
    }
}