package com.marketplace.marketplace.authentication;

import com.marketplace.marketplace.authentication.requests.AuthenticationRequest;
import com.marketplace.marketplace.authentication.requests.RegistrationRequest;
import com.marketplace.marketplace.authentication.requests.UsernameRequest;
import com.marketplace.marketplace.exception.InvalidOperationException;
import com.marketplace.marketplace.exception.InvalidTokenException;
import com.marketplace.marketplace.exception.UserNotFoundException;
import com.marketplace.marketplace.jwt.Jwt;
import com.marketplace.marketplace.jwt.JwtService;
import com.marketplace.marketplace.token.ConfirmationToken;
import com.marketplace.marketplace.token.TokenService;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User registerCustomer(RegistrationRequest request) {

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(hashedPassword)
                .phoneNumber(request.getPhoneNumber())
                .isEnabled(false)
                .build();

        return userRepository.save(user);
    }

    public void enableUserAccount(String token) {
        ConfirmationToken confirmationToken = tokenService.findConfirmationTokenByToken(token);
        User user = confirmationToken.getUser();

        if(tokenService.isTokenNotValid(confirmationToken)){
            log.error("Invalid confirmation token");
            throw new InvalidTokenException("Invalid Confirmation Token");
        }

        // enable the user and set the confirmation date to the current time
        user.setEnabled(true);
        confirmationToken.setConfirmedAt(new Date());

        userRepository.save(user);
        tokenService.saveConfirmationToken(confirmationToken);

        log.info(String.format("%s has enabled his account", user.getUsername()));
    }

    public User deletePreviousConfirmationToken(UsernameRequest request) {
        String username = request.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Username not found"));

        if(user.isEnabled())
            throw new InvalidOperationException("User is already enabled");

        ConfirmationToken userConfirmationToken = tokenService.findConfirmationTokenByUser(user);
        tokenService.deleteConfirmationToken(userConfirmationToken);
        return user;
    }

    public AuthenticationResponse authenticate(
            AuthenticationRequest request,
            HttpServletResponse response
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        jwtService.revokeAllTokens(user);
        // generate the tokens and save them to the database
        Jwt jwtAuth = jwtService.generateAccessToken(user);
        Jwt jwtRefresh = jwtService.generateRefreshToken(user);

        jwtService.saveJwt(jwtAuth);
        jwtService.saveJwt(jwtRefresh);

        // save the refresh token in an httpOnly cookie
        Cookie cookie = new Cookie("refreshToken", jwtRefresh.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return AuthenticationResponse.builder()
                .accessToken(jwtAuth.getToken())
                .build();
    }

    public AuthenticationResponse refreshToken(String token) {

        if (token == null) {
            log.error("No http only cookie was provided");
            throw new InvalidOperationException("No http only cookie was provided");
        }

        // the token doesn't exist in the db
        Jwt refreshToken = jwtService.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Jwt is not valid"));

        // the token must be a refresh token and a valid one
        if(!jwtService.validateRefreshToken(refreshToken))
            throw new InvalidTokenException("Invalid or not refresh token provided");

        User user = refreshToken.getUser();
        // revoke all the previous access tokens then generate a new one and save it to the database
        jwtService.revokeAllAccessTokens(user);
        Jwt accessToken = jwtService.generateAccessToken(user);
        jwtService.saveJwt(accessToken);

        log.info("New access token was generated");

        return AuthenticationResponse.builder()
                .accessToken(accessToken.getToken())
                .build();
    }
}
