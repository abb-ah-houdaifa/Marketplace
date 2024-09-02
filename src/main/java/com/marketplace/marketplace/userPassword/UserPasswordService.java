package com.marketplace.marketplace.userPassword;

import com.marketplace.marketplace.exception.InvalidTokenException;
import com.marketplace.marketplace.exception.UserNotFoundException;
import com.marketplace.marketplace.token.ResetPasswordToken;
import com.marketplace.marketplace.token.TokenService;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.UserRepository;
import com.marketplace.marketplace.userPassword.requests.ChangePasswordRequest;
import com.marketplace.marketplace.userPassword.requests.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public void changeUserPassword(
            ChangePasswordRequest request,
            Authentication authentication
    ) {
        String username = authentication.getPrincipal().toString(); // get the username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()
        ))
            throw new BadCredentialsException("Incorrect old password");

        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newHashedPassword);
        userRepository.save(user);
        log.info("User with ID : {} has changed his password", user.getId());
    }

    public void resetUserPassword(String token, ResetPasswordRequest request) {
        ResetPasswordToken resetPasswordToken = tokenService.findResetPasswordTokenByToken(token);
        User user = resetPasswordToken.getUser();

        if (
                tokenService.isTokenNotValid(resetPasswordToken) ||
                resetPasswordToken.isRevoked()
        ) {
            throw new InvalidTokenException("Invalid Confirmation Token");
        }

        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newHashedPassword);
        userRepository.save(user);

        // update the confirmed at of the reset password token
        resetPasswordToken.setConfirmedAt(new Date());
        tokenService.saveResetPasswordToken(resetPasswordToken);

        log.info("User with ID: {} has changed his password", user.getId());
    }
}
