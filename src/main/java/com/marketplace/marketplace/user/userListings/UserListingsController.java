package com.marketplace.marketplace.user.userListings;

import com.marketplace.marketplace.item.Item;
import com.marketplace.marketplace.utils.modelAssembler.UserListingModelAssembler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/listings")
public class UserListingsController {
    private final UserListingService userListingService;
    private final UserListingModelAssembler userListingModelAssembler;

    @GetMapping("userId/{userId}")
    public ResponseEntity<?> getUserListing(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> userListings = userListingService.getUserListings(userId, pageable);
        return new ResponseEntity<>(
                userListingModelAssembler.toCollectionModel(
                        userListings,
                        request
                ),
                HttpStatus.OK
        );
    }

    // get the listings of the current authenticated user
    @GetMapping("")
    public ResponseEntity<?> getAuthenticatedUserListing(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> userListings = userListingService.getAuthenticatedUserListings(authentication, pageable);
        return new ResponseEntity<>(
                userListingModelAssembler.toCollectionModel(
                        userListings,
                        request
                ),
                HttpStatus.OK
        );
    }

    @PostMapping(value = "add-listing", consumes = "multipart/form-data")
    public ResponseEntity<?> addListing(
            @RequestPart("images") List<MultipartFile> itemImages,
            @Valid @RequestPart("item") ListingRequest request,
            Authentication authentication
    ) {
        Item item = userListingService.saveUserListing(
                request,
                itemImages,
                authentication
        );
        return new ResponseEntity<>(userListingModelAssembler.toModel(item), HttpStatus.OK);
    }

    @PutMapping("{itemId}/add-images")
    public ResponseEntity<?> addImageToListing(
            @PathVariable("itemId") Long itemId,
            @RequestParam("images") List<MultipartFile> newItemImages,
            Authentication authentication
    ) {
        try {
            Item item = userListingService.addImagesToListing(
                    itemId,
                    newItemImages,
                    authentication
            );
            return new ResponseEntity<>(userListingModelAssembler.toModel(item), HttpStatus.OK);
        } catch (IOException ex){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/{itemId}/edit-listing")
    public ResponseEntity<?> editListingDetails (
            @PathVariable("itemId") Long itemId,
            @RequestBody @Valid ListingRequest newListingDetails,
            Authentication authentication
    ) {
        try {
            Item item = userListingService.editListingDetails(
                    itemId,
                    newListingDetails,
                    authentication
            );
            return new ResponseEntity<>(userListingModelAssembler.toModel(item), HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteListing(
            @PathVariable("itemId") Long itemId,
            Authentication authentication
    ) {
        try {
            userListingService.deleteListing(itemId, authentication);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
