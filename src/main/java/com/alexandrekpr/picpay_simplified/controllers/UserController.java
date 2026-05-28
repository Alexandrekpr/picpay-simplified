package com.alexandrekpr.picpay_simplified.controllers;

import java.util.List;

import com.alexandrekpr.picpay_simplified.dtos.PaginatedResponse;
import com.alexandrekpr.picpay_simplified.dtos.UserResponse;
import com.alexandrekpr.picpay_simplified.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.UserRequest;
import com.alexandrekpr.picpay_simplified.exceptions.NotFoundException;
import com.alexandrekpr.picpay_simplified.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  };

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user) {
    UserResponse createdUser = userService.createUser(user);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @GetMapping
  public Page<UserResponse> getUsers(@PageableDefault(page = 0, size = 10) Pageable pageable) {
    return userService.getAllUsers(pageable);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) throws NotFoundException {
    User user = userService.findById(id);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}