package com.marketplace.marketplace.token.event;

import com.marketplace.marketplace.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ResetPasswordEvent extends ApplicationEvent {
    private final User user;

    public ResetPasswordEvent(User source) {
        super(source);
        this.user = source;
    }
}
