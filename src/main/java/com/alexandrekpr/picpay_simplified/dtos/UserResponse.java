package com.alexandrekpr.picpay_simplified.dtos;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;

import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String name,
        String email,
        BigDecimal balance,
        UserType type
) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getBalance(),
            user.getType()
        );
    }
}
