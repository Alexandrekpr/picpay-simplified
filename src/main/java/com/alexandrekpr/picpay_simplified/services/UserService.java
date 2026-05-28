package com.alexandrekpr.picpay_simplified.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.alexandrekpr.picpay_simplified.dtos.PaginatedResponse;
import com.alexandrekpr.picpay_simplified.dtos.UserResponse;
import com.alexandrekpr.picpay_simplified.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;
import com.alexandrekpr.picpay_simplified.dtos.UserRequest;
import com.alexandrekpr.picpay_simplified.repositories.UserRepository;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  public void validateTransaction(User sender, BigDecimal amount) throws ForbiddenException, InsufficientFundsException {
    if (sender.getType() == UserType.MERCHANT) {
      throw new ForbiddenException();
    }

    if (sender.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException("Insufficient funds.");
    }
  }

  public User findById(Long id) throws NotFoundException {
    return userRepository
    .findUserById(id)
    .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
  }

  public UserResponse createUser(UserRequest data) {
    boolean userExists = userRepository.existsUserByEmail(data.email());
    if (userExists) {
      throw new BadRequestException("User already exists");
    }

    User user = data.toEntity();
    User newUser = userRepository.save(user);
    return UserResponse.fromEntity(newUser);
  }

  public Page<UserResponse> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(UserResponse::fromEntity);
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }
}