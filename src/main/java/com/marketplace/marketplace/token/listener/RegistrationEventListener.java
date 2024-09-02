package com.marketplace.marketplace.token.listener;

import com.marketplace.marketplace.token.ConfirmationToken;
import com.marketplace.marketplace.token.TokenService;
import com.marketplace.marketplace.token.event.RegistrationEvent;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.utils.MailSenderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationEventListener implements ApplicationListener<RegistrationEvent> {
    private final TokenService tokenService;
    private final MailSenderService mailSenderService;
    @Value("${spring.application.context-path}")
    private String applicationContextPath;

    @Override
    public void onApplicationEvent(@NonNull RegistrationEvent event) {
        final String endpointPath = "confirm-registration";

        String token = UUID.randomUUID().toString();
        User user = event.getUser();

        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        tokenService.saveConfirmationToken(confirmationToken);

        String confirmationLink = String.format("%s/%s?token=%s",applicationContextPath,endpointPath,token);
        mailSenderService.sendConfirmationLink(
                confirmationLink,
                user.getUsername()
        );
        log.info("A confirmation link was sent to user");
    }
}
