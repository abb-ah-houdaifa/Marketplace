package com.marketplace.marketplace.user.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemUserResponse extends RepresentationModel<ItemUserResponse> {
    private Long id;
    private Link profileImageLink;
}
