package com.alexandrekpr.picpay_simplified.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexandrekpr.picpay_simplified.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);
    boolean existsUserByEmail(String email);

    Optional<User> findUserByDocument(String document);
}
