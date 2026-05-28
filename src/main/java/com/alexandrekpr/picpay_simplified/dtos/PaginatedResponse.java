package com.alexandrekpr.picpay_simplified.dtos;

public record PaginatedResponse<T>(
  int count,
  int page,
  int pageSize,
  T response
){}
