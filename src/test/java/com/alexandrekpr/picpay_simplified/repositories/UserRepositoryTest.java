package com.alexandrekpr.picpay_simplified.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.domain.user.UserType;
import com.alexandrekpr.picpay_simplified.dtos.UserRequest;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("should get User successfully from db")
    void findUserByIdCase1() {
        UserRequest data = new UserRequest(
                "Alexandre",
                "99999999901",
                new BigDecimal(10),
                "alexandre@email.com",
                "password",
                UserType.COMMON
        );
        User createdUser = this.createUser(data);

        Optional<User> result = this.userRepository.findUserById(createdUser.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("should not get User from db when user not exists")
    void findUserByIdCase2() {
        Optional<User> result = this.userRepository.findUserById(new Long(1234567));
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should return true when user exists with this email")
    void existsUserByEmailCase1() {
        String email = "alexandre@email.com";
        UserRequest data = new UserRequest(
                "Alexandre",
                "99999999901",
                new BigDecimal(10),
                email,
                "password",
                UserType.COMMON
        );
        User createdUser = this.createUser(data);

        Boolean result = this.userRepository.existsUserByEmail(email);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when user not exists with this email")
    void existsUserByEmailCase2() {
        Boolean result = this.userRepository.existsUserByEmail("xande@email.com");
        assertThat(result).isFalse();
    }

    private User createUser(UserRequest data) {
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}