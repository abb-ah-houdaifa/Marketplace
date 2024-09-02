package com.marketplace.marketplace.token.repository;

import com.marketplace.marketplace.token.ResetPasswordToken;
import com.marketplace.marketplace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);

    List<ResetPasswordToken> findAllByUser(User user);
}
