package com.marketplace.marketplace.user.userProfile;


import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.utils.modelAssembler.UserModelAssembler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final UserModelAssembler userModelAssembler;

    @GetMapping("/userId/{userId}")
    public ResponseEntity<?> getUserProfile(
            @PathVariable("userId") Long userId
    ){
        User user = userProfileService.getUserProfile(userId);
        return new ResponseEntity<>(userModelAssembler.toModel(user), HttpStatus.OK);
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getUserProfileByAuthentication(
            Authentication authentication
    ) {
        User user = userProfileService.getUserProfileByAuthentication(authentication);
        return new ResponseEntity<>(userModelAssembler.toModel(user), HttpStatus.OK);
    }

    @PutMapping("/upload-profile-image")
    public ResponseEntity<?> editUserProfileImage(
            @RequestParam("image") MultipartFile profileImage,
            Authentication authentication
    ){
        try{
            userProfileService.editUserProfileImage(authentication, profileImage);
            return new ResponseEntity<>("Profile image uploaded", HttpStatus.OK);
        } catch (IOException ex){
            return new ResponseEntity<>("Profile image couldn't be changed",HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // this controller is used both for uploading a new image profile and editing an old one
    @PutMapping("/edit-profile")
    public ResponseEntity<?> editUserProfileDetails(
            Authentication authentication,
            @Valid @RequestBody EditProfileRequest request
    ){
        User editedUser = userProfileService.editUserProfileDetails(authentication, request);
        return new ResponseEntity<>(userModelAssembler.toModel(editedUser) ,HttpStatus.OK);
    }
}
