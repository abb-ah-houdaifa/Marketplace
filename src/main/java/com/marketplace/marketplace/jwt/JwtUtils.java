package com.marketplace.marketplace.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${spring.application.security.jwt.signing-key}")
    private String signingKey;
    @Value("${spring.application.security.jwt.expiration-time-ms}")
    private long authTokenExpirationTime;
    @Value("${spring.application.security.refresh.expiration-time-ms}")
    private long refreshTokenExpirationTime;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // extract a single claim from the jwt token
    private <T> T extractClaim(String token, Function<Claims, T> function){
        return function.apply(extractAllClaims(token));
    }

    //extract all the claims from the jwt token
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] key = Base64.getDecoder().decode(signingKey);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && isExpired(token);
    }

    private boolean isExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.after(new Date());
    }

    private String createJwt(
            String username,
            Map<String, Object> claims,
            long expirationTime
    ){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .claims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAuthJwt(String username){
        return createJwt(
                username,
                new HashMap<>(),
                authTokenExpirationTime
        );
    }

    public String generateRefreshJwt(String username){
        return createJwt(
                username,
                new HashMap<>(),
                refreshTokenExpirationTime
        );
    }
}
