package com.marketplace.marketplace.item;

import com.marketplace.marketplace.user.responses.ItemUserResponse;
import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@Builder
public class ItemResponse extends RepresentationModel<ItemResponse> {
    private Long id;
    private String itemName;
    private String itemDescription;
    private Integer itemPrice;
    private ItemCondition itemCondition;
    private Date publishedAt;
    private ItemUserResponse user;
    private List<Link> imagesLinks;
}
