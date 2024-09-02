package com.marketplace.marketplace.token.repository;

import com.marketplace.marketplace.token.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    @Query(
            value = """
                SELECT *
                FROM confirmation_token t
                WHERE t.user_id = ?1
            """,
            nativeQuery = true
    )
    Optional<ConfirmationToken> findByUserId(Long id);
}
