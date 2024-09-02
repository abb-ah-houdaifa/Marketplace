package com.marketplace.marketplace.userPassword.requests;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
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
    private String newPassword;
}
