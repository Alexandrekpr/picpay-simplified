package com.alexandrekpr.picpay_simplified.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.alexandrekpr.picpay_simplified.dtos.Exception;

@RestControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Exception> handleDuplicatedUser() { 
    Exception exception = new Exception("User already exists", "400");
    return ResponseEntity.badRequest().body(exception);
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<Exception> handleInsufficientFunds(InsufficientFundsException ex) {
    Exception exception = new Exception(ex.getMessage(), "400");
    return ResponseEntity.badRequest().body(exception);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Exception> handleEntityNotFound(NotFoundException ex) {
    Exception exception = new Exception(ex.getMessage(), "404");
    return ResponseEntity.status(404).body(exception);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Exception> handleForbidden() {
    Exception exception = new Exception("Forbidden", "403");
    return ResponseEntity.status(403).body(exception);
  }

  @ExceptionHandler(java.lang.Exception.class)
  public ResponseEntity<Exception> handleGenericException(java.lang.Exception ex) {
    Exception exception = new Exception(ex.getMessage(), "500");
    return ResponseEntity.status(500).body(exception);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Exception> handleBadRequestException(BadRequestException ex) {
    Exception exception = new Exception(ex.getMessage(), "400");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<Exception> handleExternalApiError(HttpClientErrorException ex) {
    if (ex.getStatusCode().value() == 403) {
        Exception exception = new Exception("Transaction not approved", "403");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception);
    }

    if (ex.getStatusCode().value() == 504 || ex.getStatusCode().value() == 502) {
        Exception exception = new Exception("The payment/notification service is unstable. Please try again later.", "502");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception);
    }

    Exception exception = new Exception("External service unavailable at the moment", "502");
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception);
}
}
