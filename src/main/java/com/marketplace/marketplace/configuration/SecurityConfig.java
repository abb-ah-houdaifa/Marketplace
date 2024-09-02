package com.marketplace.marketplace.configuration;

import com.marketplace.marketplace.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final static String[] WHITE_LIST_API = {
            "api/user/auth/**",
            "/api/user/logout",
            "api/items/**",
            "api/user/password/reset-password",
            "api/user/password/forgot-password",
            "api/user/profile/userId/**",
            "api/user/listings/userId/**",
            "api/image/**",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };
    private final JwtFilter oncePerRequestFilter;
    private final LogoutServiceHandler logoutServiceHandler;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(WHITE_LIST_API).permitAll()
                        .requestMatchers("/api/user/profile/**", OPTIONS.name()).permitAll() // allow options requests sent automatically by the front-end to access the put requests
                        .requestMatchers("/api/user/listings/**", OPTIONS.name()).permitAll()
                        .requestMatchers("/api/user/password/**", OPTIONS.name()).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(oncePerRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/user/logout")
                        .addLogoutHandler(logoutServiceHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()));

        return http.build();
    }
}
