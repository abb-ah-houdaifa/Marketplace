package com.marketplace.marketplace.token.event;

import com.marketplace.marketplace.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationEvent extends ApplicationEvent {
    private final User user;

    public RegistrationEvent(User source) {
        super(source);
        this.user = source;
    }
}
