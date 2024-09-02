package com.marketplace.marketplace.authentication;

import com.marketplace.marketplace.authentication.requests.AuthenticationRequest;
import com.marketplace.marketplace.authentication.requests.RegistrationRequest;
import com.marketplace.marketplace.authentication.requests.UsernameRequest;
import com.marketplace.marketplace.token.event.RegistrationEvent;
import com.marketplace.marketplace.user.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher publisher;

    // save the user to the db and send a confirmation link
    @PostMapping("/sign-in")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegistrationRequest request
    ){
        User user = authenticationService.registerCustomer(request);
        publisher.publishEvent(
                new RegistrationEvent(user)
        );
        return new ResponseEntity<>("A link has been sent to your email", HttpStatus.CREATED);
    }

    // resend the confirmation link to the user
    @PostMapping("/resend-link")
    public ResponseEntity<?> resendConfirmationLink(
            @Valid @RequestBody UsernameRequest request
    ){
        User user = authenticationService.deletePreviousConfirmationToken(request);
        publisher.publishEvent(
                new RegistrationEvent(user)
        );
        return new ResponseEntity<>("Another link has been sent to your email",HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ){
        AuthenticationResponse authResponse = authenticationService.authenticate(
                request,
                response
        );
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> enableUserAccount(
            @RequestParam("token") String token
    ){
        authenticationService.enableUserAccount(token);
        return new ResponseEntity<>("Your account is enabled", HttpStatus.OK);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshJwt(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
