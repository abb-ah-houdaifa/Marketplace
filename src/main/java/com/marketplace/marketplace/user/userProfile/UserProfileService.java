package com.marketplace.marketplace.user.userProfile;

import com.marketplace.marketplace.image.Image;
import com.marketplace.marketplace.image.ImageService;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {
    private final UserService userService;
    private final ImageService imageService;

    public User getUserProfile(Long userId) {
        return userService.findUserById(userId);
    }

    public User getUserProfileByAuthentication(Authentication authentication) {
        return userService.extractUserFromAuthentication(authentication);
    }

    public void editUserProfileImage(
            Authentication authentication,
            MultipartFile profileImage
    ) throws IOException {
        User user = userService.extractUserFromAuthentication(authentication);
        // the user doesn't have a previous profile picture
        if(user.getImage() == null){
            Image image = imageService.saveProfileImage(profileImage);
            user.setImage(image);
            userService.save(user);
            return;
        }

        Image previousProfileImage = user.getImage();
        imageService.editProfileImage(previousProfileImage, profileImage);
    }

    public User editUserProfileDetails(
            Authentication authentication,
            EditProfileRequest request
    ) {
        User user = userService.extractUserFromAuthentication(authentication);

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());

        return userService.save(user);
    }
}
