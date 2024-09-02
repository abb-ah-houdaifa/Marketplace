package com.marketplace.marketplace.utils.modelAssembler;

import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.responses.UserResponse;
import com.marketplace.marketplace.user.userProfile.UserProfileController;
import com.marketplace.marketplace.utils.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class UserModelAssembler implements RepresentationModelAssembler<User, UserResponse> {
    private final Mapper mapper;

    @Override
    public UserResponse toModel(User user) {
        UserResponse userResponse = mapper.mapUserToResponse(user);
        Link selfLink = linkTo(methodOn(UserProfileController.class).getUserProfile(user.getId())).withSelfRel();
        userResponse.add(selfLink);

        return userResponse;
    }
}
