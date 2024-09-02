package com.marketplace.marketplace.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.application.name}")
    private String applicationName;

    public void sendConfirmationLink(
            String link,
            String username
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(username);
            messageHelper.setFrom(new InternetAddress(from));
            messageHelper.setSubject(String.format("[%s] Enable your account", applicationName));

            Resource resource = new ClassPathResource("templates/confirm-registration-email.html");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));

            content = content.replace("{{confirmation_link}}",link);
            content = content.replace("{{username}}",username);
            content = content.replace("{{application-name}}",applicationName);

            messageHelper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException exception) {
            log.error("Error while sending email to : {}",username);
            throw new RuntimeException(exception);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void sendResetPasswordLink(
            String link,
            String username
    ){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(username);
            messageHelper.setFrom(new InternetAddress(from));
            messageHelper.setSubject(String.format("[%s] Enable your account", applicationName));

            Resource resource = new ClassPathResource("templates/reset-password-email.html");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));

            content = content.replace("{{reset_link}}",link);
            content = content.replace("{{username}}",username);

            messageHelper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException exception) {
            log.error("Error while sending email to : {}",username);
            throw new RuntimeException(exception);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
