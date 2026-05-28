package com.alexandrekpr.picpay_simplified.dtos;

import java.math.BigDecimal;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;

public record UserRequest(
        String name,
        String document,
        BigDecimal balance,
        String email,
        String password,
        UserType type
) {

    public User toEntity() {
        User user = new User();
        fill(user);
        return user;
    }

    public void fill(User user) {
        user.setName(name);
        user.setDocument(document);
        user.setBalance(balance);
        user.setEmail(email);
        user.setPassword(password);
        user.setType(type);
    }
}
