package com.marketplace.marketplace.token;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class Token {
    private final static long EXPIRATION_TIME_MS = 900000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    private Date confirmedAt;
    private Date expiresAt;

    public Token(String token){
        this.token = token;
        this.expiresAt = calculateExpirationDate();
    }

    private Date calculateExpirationDate(){
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS);
    }
}
