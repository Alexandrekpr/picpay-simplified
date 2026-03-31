package com.alexandrekpr.picpay_simplified.dtos;

import java.math.BigDecimal;

public record TransactionDTO(BigDecimal value, Long senderId, Long receiverId) {  
}
