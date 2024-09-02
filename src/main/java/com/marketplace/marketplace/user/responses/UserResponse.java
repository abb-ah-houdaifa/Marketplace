package com.marketplace.marketplace.user.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserResponse extends RepresentationModel<UserResponse> {
    private Long id;
    private String name;
    private String phoneNumber;
    private Link profileImageLink;
}
