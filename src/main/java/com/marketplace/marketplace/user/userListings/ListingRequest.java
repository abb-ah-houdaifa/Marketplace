package com.marketplace.marketplace.user.userListings;

import com.marketplace.marketplace.item.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListingRequest {
    @NotBlank(message = "Item name can't be blank")
    private String itemName;
    @NotNull(message = "Item description must can't be null")
    private String itemDescription;
    @NotNull(message = "Item price can't be null")
    private Integer itemPrice;
    @NotNull(message = "Item condition can't be null")
    private ItemCondition itemCondition;
}
