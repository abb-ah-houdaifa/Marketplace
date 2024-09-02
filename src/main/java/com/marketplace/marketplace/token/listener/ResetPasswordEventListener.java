package com.marketplace.marketplace.token.listener;

import com.marketplace.marketplace.token.ResetPasswordToken;
import com.marketplace.marketplace.token.TokenService;
import com.marketplace.marketplace.token.event.ResetPasswordEvent;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.utils.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordEventListener implements ApplicationListener<ResetPasswordEvent> {
    private final MailSenderService mailSenderService;
    private final TokenService tokenService;

    @Value("${spring.application.context-path}")
    private String applicationContextPath;

    @Override
    public void onApplicationEvent(ResetPasswordEvent event) {
        final String endpoint = "reset-password";

        String token = UUID.randomUUID().toString();
        User user = event.getUser();

        // revoke all previous reset password tokens
        tokenService.revokeAllResetPasswordTokens(user);

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, user);
        tokenService.saveResetPasswordToken(resetPasswordToken);

        String resetLink = String.format("%s/%s?token=%s", applicationContextPath, endpoint, token);
        mailSenderService.sendResetPasswordLink(
                resetLink,
                user.getUsername()
        );
        log.info("A confirmation link was sent to user");
    }
}
