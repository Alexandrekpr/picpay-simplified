package com.alexandrekpr.picpay_simplified.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.alexandrekpr.picpay_simplified.dtos.ExceptionDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionDTO> handleDuplicatedUser() {
    ExceptionDTO exceptionDTO = new ExceptionDTO("User already exists", "400");
    return ResponseEntity.badRequest().body(exceptionDTO);
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<ExceptionDTO> handleInsufficientFunds(InsufficientFundsException ex) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), "400");
    return ResponseEntity.badRequest().body(exceptionDTO);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ExceptionDTO> handleEntityNotFound(EntityNotFoundException ex) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), "404");
    return ResponseEntity.status(404).body(exceptionDTO);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ExceptionDTO> handleForbidden(ForbiddenException ex) {
    ExceptionDTO exceptionDTO = new ExceptionDTO("Forbidden", "403");
    return ResponseEntity.status(403).body(exceptionDTO);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionDTO> handleGenericException(Exception ex) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), "500");
    return ResponseEntity.status(500).body(exceptionDTO);
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ExceptionDTO> handleExternalApiError(HttpClientErrorException ex) {
    if (ex.getStatusCode().value() == 403) {
        ExceptionDTO exceptionDTO = new ExceptionDTO("Transaction not approved", "403");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDTO);
    }

    if (ex.getStatusCode().value() == 504 || ex.getStatusCode().value() == 502) {
        ExceptionDTO exceptionDTO = new ExceptionDTO("The payment/notification service is unstable. Please try again later.", "502");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionDTO);
    }

    ExceptionDTO exceptionDTO = new ExceptionDTO("External service unavailable at the moment", "502");
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionDTO);
}
}
