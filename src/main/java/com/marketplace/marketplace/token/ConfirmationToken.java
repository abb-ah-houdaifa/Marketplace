package com.marketplace.marketplace.token;

import com.marketplace.marketplace.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// Once the user registers to the application
// A confirmation link will be sent to its email
// Which contains a unique token
// And by clicking the link the user's account will be enabled
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Data
public class ConfirmationToken extends Token{
    // we want to keep only one token for each user
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ConfirmationToken(
            String token,
            User user
    ){
        super(token);
        this.user = user;
    }
}
