package com.marketplace.marketplace.userPassword;

import com.marketplace.marketplace.token.event.ResetPasswordEvent;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.UserService;
import com.marketplace.marketplace.userPassword.requests.ChangePasswordRequest;
import com.marketplace.marketplace.userPassword.requests.ForgotPasswordRequest;
import com.marketplace.marketplace.userPassword.requests.ResetPasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/password")
public class UserPasswordController {
    private final UserPasswordService userPasswordService;
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/change-password")
    public ResponseEntity<?> changeUserPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ){
        userPasswordService.changeUserPassword(request, authentication);
        return new ResponseEntity<>("Password changed", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetPasswordLink(
            @Valid @RequestBody ForgotPasswordRequest request
    ){
        User user = userService.findUserByUsername(request.getUsername());
        publisher.publishEvent(
                new ResetPasswordEvent(user)
        );
        return new ResponseEntity<>("A reset password link was sent to your email", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetUserPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody ResetPasswordRequest request
    ){
        userPasswordService.resetUserPassword(token, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
