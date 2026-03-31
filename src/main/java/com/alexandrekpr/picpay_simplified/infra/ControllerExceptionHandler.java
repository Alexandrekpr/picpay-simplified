package com.alexandrekpr.picpay_simplified.infra;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alexandrekpr.picpay_simplified.dtos.ExceptionDTO;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionDTO> threatDuplicateEntity() {
    ExceptionDTO exceptionDTO = new ExceptionDTO("User already exists", "400");
    return ResponseEntity.badRequest().body(exceptionDTO);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Exception> threat404(EntityNotFoundException ex) {
    return ResponseEntity.notFound().build();
  }

  //TODO: Adicionar mais tratamentos de exceção aqui, como por exemplo para transações com saldo insuficiente, ou para erros de autenticação (não implementado)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionDTO> threatGeneralException(Exception ex) {
    ExceptionDTO exceptionDTO = new ExceptionDTO(ex.getMessage(), "500");
    return ResponseEntity.status(500).body(exceptionDTO);
  }
}
