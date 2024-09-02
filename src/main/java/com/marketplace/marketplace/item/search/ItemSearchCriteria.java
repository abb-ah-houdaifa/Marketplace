package com.marketplace.marketplace.item.search;

import lombok.Data;

@Data
public class ItemSearchCriteria {
    private String filterKey;
    private String value;
    private String operation;
}
