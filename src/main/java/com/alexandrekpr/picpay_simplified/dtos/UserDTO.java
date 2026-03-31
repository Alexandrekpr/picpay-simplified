package com.alexandrekpr.picpay_simplified.dtos;

import java.math.BigDecimal;

import com.alexandrekpr.picpay_simplified.domain.user.UserType;

public record UserDTO(String name, String document, BigDecimal balance, String email, String password, UserType type) {}
