package com.marketplace.marketplace.user.userListings;

import com.marketplace.marketplace.exception.InvalidOperationException;
import com.marketplace.marketplace.item.Item;
import com.marketplace.marketplace.item.ItemService;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserListingService {
    private final UserService userService;
    private final ItemService itemService;

    public Page<Item> getUserListings(Long userId, Pageable pageable) {
        User user = userService.findUserById(userId);
        return itemService.getRandomListingsByUser(user, pageable);
    }

    // get the current authenticated user listings
    public Page<Item> getAuthenticatedUserListings(
            Authentication authentication,
            Pageable pageable
    ) {
        User user = userService.extractUserFromAuthentication(authentication);
        return itemService.getUserListings(user, pageable);
    }

    public Item saveUserListing(
            ListingRequest request,
            List<MultipartFile> itemImages,
            Authentication authentication
    ) {
        User user = userService.extractUserFromAuthentication(authentication);
        Item item = itemService.saveItem(
                user,
                itemImages,
                request
        );
        log.info("User with ID: {} added a listing", user.getId());
        return item;
    }

    // checks whether the user requesting a listing change owns the listing
    private Item assertThatListingBelongsToCorrectUser(
            Authentication authentication,
            Long itemId
    ){
        User user = userService.extractUserFromAuthentication(authentication);
        Item item = itemService.findItemById(itemId);

        // ensure that the item to be modified belong to the correct user
        if(!item.getUser().equals(user)){
            log.warn("User with ID :{} tried to modify an item with ID: {} which does belong to him", user.getId(), itemId);
            String errorMessage = String.format(
                    "Item with ID : %s does not belong to user with ID : %s",
                    itemId,
                    user.getId()
            );
            throw new InvalidOperationException(errorMessage);
        }

        return item;
    }

    public Item addImagesToListing(
            Long itemId,
            List<MultipartFile> newItemImages,
            Authentication authentication
    ) throws IOException {
        Item item = this.assertThatListingBelongsToCorrectUser(authentication, itemId);
        Item savedItem = itemService.addImagesToItem(item, newItemImages);

        log.info("Images were added to the item with ID : {}", itemId);
        return savedItem;
    }

    public Item editListingDetails(
            Long itemId,
            ListingRequest newListingDetails,
            Authentication authentication
    ) throws IOException{
        Item item = this.assertThatListingBelongsToCorrectUser(authentication, itemId);
        Item editedItem = itemService.editListingDetails(item, newListingDetails);

        log.info("Item with ID : {} was modified", itemId);
        return  editedItem;
    }

    public void deleteListing(
            Long itemId,
            Authentication authentication
    ) throws IOException {
        Item item = assertThatListingBelongsToCorrectUser(authentication, itemId);

        itemService.deleteListing(item);
        log.info("Item with Id : {} was deleted", item.getId());
    }
}
