package com.marketplace.marketplace.jwt;

import com.marketplace.marketplace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtRepository jwtRepository;
    private final JwtUtils jwtUtils;

    public boolean validateToken(Jwt jwt){
        return  jwt != null && jwtUtils.isTokenValid(jwt.getToken(), jwt.getUser()) && !jwt.isRevoked();
    }

    public boolean validateRefreshToken(Jwt refreshToken) {
        return validateToken(refreshToken) && refreshToken.getType() == JwtType.REFRESH;
    }

    public Optional<Jwt> findByToken(String token) {
        return jwtRepository.findByToken(token);
    }

    public Jwt generateRefreshToken(User user){
        String token = jwtUtils.generateRefreshJwt(user.getUsername());
        return Jwt.builder()
                .user(user)
                .isRevoked(false)
                .token(token)
                .type(JwtType.REFRESH)
                .build();
    }

    public Jwt generateAccessToken(User user){
        String token = jwtUtils.generateAuthJwt(user.getUsername());
        return Jwt.builder()
                .user(user)
                .isRevoked(false)
                .token(token)
                .type(JwtType.ACCESS)
                .build();
    }

    public void revokeAllTokens(User user){
        List<Jwt> validTokens = this.fetchAllValidTokens(user.getId());
        validTokens.forEach(token -> token.setRevoked(true));
        jwtRepository.saveAll(validTokens);
    }

    private List<Jwt> fetchAllValidAccessTokens(Long userId){
        return jwtRepository.findAllValidAccessTokens(userId);
    }

    public void revokeAllAccessTokens(User user){
        List<Jwt> validAccessTokens = this.fetchAllValidAccessTokens(user.getId());
        validAccessTokens.forEach(token -> token.setRevoked(true));
        jwtRepository.saveAll(validAccessTokens);
    }

    private List<Jwt> fetchAllValidTokens(Long userId){
        return jwtRepository.findAllValidTokens(userId);
    }

    public void saveJwt(Jwt token){
        jwtRepository.save(token);
    }
}
