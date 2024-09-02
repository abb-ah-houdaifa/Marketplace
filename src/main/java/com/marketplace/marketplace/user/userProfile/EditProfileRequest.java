package com.marketplace.marketplace.user.userProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileRequest {
    @NotBlank
    private String name;
    @Pattern(
            regexp = "^0([567])\\d{8}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;
}
