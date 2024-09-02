package com.marketplace.marketplace.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {
    Optional<Jwt> findByToken(String token);

    @Query(
            """
                SELECT t FROM Jwt t JOIN t.user u WHERE t.type = 'ACCESS' AND t.isRevoked = false
            """
    )
    List<Jwt> findAllValidAccessTokens(Long userId);

    @Query(
            """
                SELECT t FROM Jwt t JOIN t.user u WHERE t.isRevoked = false
            """
    )
    List<Jwt> findAllValidTokens(Long userId);
}
