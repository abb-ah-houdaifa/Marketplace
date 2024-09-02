package com.marketplace.marketplace.configuration;

import com.marketplace.marketplace.exception.InvalidTokenException;
import com.marketplace.marketplace.jwt.Jwt;
import com.marketplace.marketplace.jwt.JwtService;
import com.marketplace.marketplace.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutServiceHandler implements LogoutHandler {
    private final JwtService jwtService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.warn("No cookies provided in the logout request");
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("refreshToken")) {
                Jwt refreshToken = jwtService.findByToken(cookie.getValue())
                        .orElseThrow(() -> new InvalidTokenException("Refresh token doesn't exist in the database"));

                if (!jwtService.validateRefreshToken(refreshToken)) {
                    log.error("The token is not a valid JWT refresh token");
                    throw new InvalidTokenException("The token is not a valid JWT refresh token");
                }

                User user = refreshToken.getUser();
                jwtService.revokeAllTokens(user);

                log.info("User with ID : {} has logged out", user.getId());
                return;
            }
        }

        log.warn("No valid refresh token found for logout");
    }
}
