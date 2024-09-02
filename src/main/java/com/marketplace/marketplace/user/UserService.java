package com.marketplace.marketplace.user;

import com.marketplace.marketplace.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User With Username : %s Not Found", username)
                ));
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User With ID : %s Not Found", userId)
                ));
    }

    public User extractUserFromAuthentication(Authentication authentication){
        String username = authentication.getPrincipal().toString();
        return this.findUserByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
