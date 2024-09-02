package com.marketplace.marketplace.token;

import com.marketplace.marketplace.exception.InvalidTokenException;
import com.marketplace.marketplace.token.repository.ConfirmationTokenRepository;
import com.marketplace.marketplace.token.repository.ResetPasswordTokenRepository;
import com.marketplace.marketplace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationToken findConfirmationTokenByToken(String token){
        return this.confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid Token"));
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        this.confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken findConfirmationTokenByUser(User user){
        return this.confirmationTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new InvalidTokenException("Token not found"));
    }

    public void deleteConfirmationToken(ConfirmationToken userConfirmationToken) {
        this.confirmationTokenRepository.delete(userConfirmationToken);
    }

    public void saveResetPasswordToken(ResetPasswordToken resetPasswordToken) {
        this.resetPasswordTokenRepository.save(resetPasswordToken);
    }

    public ResetPasswordToken findResetPasswordTokenByToken(String token){
        return this.resetPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token not found"));
    }

    public void revokeAllResetPasswordTokens(
            User user
    ) {
        List<ResetPasswordToken> tokens = this.resetPasswordTokenRepository.findAllByUser(user);
        tokens.forEach(token -> token.setRevoked(true));
        resetPasswordTokenRepository.saveAll(tokens);
    }

    public boolean isTokenNotValid(Token token) {
        return
                token == null ||
                token.getExpiresAt().before(new Date()) ||
                token.getConfirmedAt() != null;
    }
}
