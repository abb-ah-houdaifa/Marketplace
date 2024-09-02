package com.marketplace.marketplace.authentication.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank(message = "Name can't be blank")
    private String name;
    @Email(message = "Username must be a valid email")
    private String username;
    @Pattern(
            regexp = "(?=^.{8,}$)(?=.*d)(?=.*[!@#$%^&*]+)(?![.n])(?=.*[A-Z])(?=.*[a-z]).*$",
            message = """
                        The password length must be greater than or equal to 8
                        The password must contain one or more uppercase characters
                        The password must contain one or more lowercase characters
                        The password must contain one or more numeric values
                        The password must contain one or more special characters
                    """
    )
    private String password;
    @NotEmpty(
            message = "Phone number can't be empty"
    )
    @Pattern(
            regexp = "^0([567])\\d{8}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;
}
