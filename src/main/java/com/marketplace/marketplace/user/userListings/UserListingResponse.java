package com.marketplace.marketplace.user.userListings;

import com.marketplace.marketplace.item.ItemCondition;
import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListingResponse extends RepresentationModel<UserListingResponse> {
    private Long id;
    private String itemName;
    private String itemDescription;
    private Integer itemPrice;
    private ItemCondition itemCondition;
    private Date publishedAt;
    private List<Link> imagesLinks;
}
