package com.marketplace.marketplace.token;

import com.marketplace.marketplace.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class ResetPasswordToken extends Token {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private boolean isRevoked = false;

    public ResetPasswordToken(
            String token,
            User user
    ){
        super(token);
        this.user = user;
    }
}
