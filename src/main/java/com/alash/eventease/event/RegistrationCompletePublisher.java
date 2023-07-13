package com.alash.eventease.event;

import com.alash.eventease.model.domain.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompletePublisher extends ApplicationEvent {

    private UserEntity user;
    private String applicationUrl;

    public RegistrationCompletePublisher(UserEntity newUser, String applicationUrl) {
        super(newUser);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }

}
