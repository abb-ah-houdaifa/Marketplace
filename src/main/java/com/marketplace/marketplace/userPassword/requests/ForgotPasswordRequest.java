package com.marketplace.marketplace.userPassword.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(
            message = "Username can't be blank"
    )
    private String username;
}
