package com.marketplace.marketplace.utils.mappers;

import com.marketplace.marketplace.image.Image;
import com.marketplace.marketplace.image.ImageController;
import com.marketplace.marketplace.item.Item;
import com.marketplace.marketplace.item.ItemResponse;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.responses.ItemUserResponse;
import com.marketplace.marketplace.user.responses.UserResponse;
import com.marketplace.marketplace.user.userListings.UserListingResponse;
import com.marketplace.marketplace.user.userProfile.UserProfileController;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class Mapper {

    private List<Link> getItemImagesLinks(List<Image> images) {
        List<Link> imagesLinks = new ArrayList<>();
        for (Image image : images) {
            Link imageLink = getImageLink(image.getImageId());
            imagesLinks.add(imageLink);
        }
        return imagesLinks;
    }

    public ItemResponse mapItemToResponse(Item item){
        List<Link> imagesLinks = getItemImagesLinks(item.getImages());

        return ItemResponse.builder()
                .id(item.getId())
                .itemName(item.getName())
                .itemDescription(item.getDescription())
                .itemCondition(item.getCondition())
                .itemPrice(item.getPrice())
                .publishedAt(item.getPublishedAt())
                .user(mapUserToItemUserResponse(item.getUser()))
                .imagesLinks(imagesLinks)
                .build();

    }


    public UserListingResponse mapItemToListingResponse(Item item){
        List<Link> imagesLinks = getItemImagesLinks(item.getImages());

        return UserListingResponse.builder()
                .id(item.getId())
                .itemName(item.getName())
                .itemDescription(item.getDescription())
                .itemCondition(item.getCondition())
                .itemPrice(item.getPrice())
                .publishedAt(item.getPublishedAt())
                .imagesLinks(imagesLinks)
                .build();

    }

    private Link getImageLink(Long imageId) {
        return linkTo(methodOn(ImageController.class).getImage(imageId)).withSelfRel();
    }

    public UserResponse mapUserToResponse(User user){
        Image userProfileImage = user.getImage();
        Link profileImageLink = null;

        if (userProfileImage != null){
            profileImageLink = getImageLink(userProfileImage.getImageId());
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profileImageLink(profileImageLink) //return the image link
                .build();
    }

    private ItemUserResponse mapUserToItemUserResponse(User user) {
        Image userProfileImage = user.getImage();
        Link profileImageLink = null;

        if (userProfileImage != null){
            profileImageLink = getImageLink(userProfileImage.getImageId());
        }

        ItemUserResponse userResponse = ItemUserResponse.builder()
                .id(user.getId())
                .profileImageLink(profileImageLink) //return the image link
                .build();

        // add self link
        userResponse.add(linkTo(methodOn(UserProfileController.class).getUserProfile(userResponse.getId())).withSelfRel());

        return userResponse;
    }
}
