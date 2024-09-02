package com.marketplace.marketplace.jwt;

import com.marketplace.marketplace.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jwts_gen")
    @SequenceGenerator(name = "jwts_gen", sequenceName = "jwts_id_gen", allocationSize = 1)
    private Long id;
    private String token;
    private boolean isRevoked;
    @Enumerated(EnumType.STRING)
    private JwtType type;
    @ManyToOne
    private User user;
}
