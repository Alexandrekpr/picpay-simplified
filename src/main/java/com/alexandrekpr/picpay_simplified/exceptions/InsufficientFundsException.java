package com.alexandrekpr.picpay_simplified.exceptions;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
        super(message);
    }
}
