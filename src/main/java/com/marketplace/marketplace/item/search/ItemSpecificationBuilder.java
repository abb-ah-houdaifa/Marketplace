package com.marketplace.marketplace.item.search;

import com.marketplace.marketplace.item.Item;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ItemSpecificationBuilder {
    private final List<ItemSearchCriteria> itemSearchCriteriaList;

    public ItemSpecificationBuilder() {
        itemSearchCriteriaList = new ArrayList<>();
    }

    public ItemSpecificationBuilder with(ItemSearchCriteria searchCriteria) {
        itemSearchCriteriaList.add(searchCriteria);
        return this;
    }

    public Specification<Item> build() {
        if (itemSearchCriteriaList.isEmpty())
            return null;

        Specification<Item> result = new ItemSpecification(itemSearchCriteriaList.get(0));

        // add up all the search filters
        for (ItemSearchCriteria searchCriteria : itemSearchCriteriaList) {
            result = Specification.where(result).and(new ItemSpecification(searchCriteria));
        }
        return result;
    }
}
