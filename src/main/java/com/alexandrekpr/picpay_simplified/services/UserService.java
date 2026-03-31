package com.alexandrekpr.picpay_simplified.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;
import com.alexandrekpr.picpay_simplified.dtos.UserDTO;
import com.alexandrekpr.picpay_simplified.repositories.UserRepository;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  public void validateTransaction(User sender, BigDecimal amount) throws Exception {
    if (sender.getType() == UserType.MERCHANT) {
      throw new Exception("Merchants cannot send money.");
    }

    if (sender.getBalance().compareTo(amount) < 0) {
      throw new Exception("Insufficient balance.");
    }
  }

  public User findById(Long id) throws Exception {
    return userRepository
    .findUserById(id)
    .orElseThrow(() -> new Exception("User not found."));
  }

  public User createUser(UserDTO data) {
    User newUser = new User(data);
    return userRepository.save(newUser);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }
}