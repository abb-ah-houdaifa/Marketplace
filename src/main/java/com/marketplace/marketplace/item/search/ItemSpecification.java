package com.marketplace.marketplace.item.search;

import com.marketplace.marketplace.exception.InvalidOperationException;
import com.marketplace.marketplace.item.Item;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
@Slf4j
public class ItemSpecification implements Specification<Item> {
    private final ItemSearchCriteria itemSearchCriteria;
    private final String[] PERMITTED_SEARCH_ATTRIBUTE = {
            "price", "description", "name", "condition"
    };

    private boolean isSearchAttributePermitted(String searchAttribute) {
        for (String attribute : PERMITTED_SEARCH_ATTRIBUTE) {
            if(attribute.equals(searchAttribute))
                return true;
        }
        return false;
    }

    @Override
    public Predicate toPredicate(
            Root<Item> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        String valueToSearch = itemSearchCriteria.getValue().toLowerCase();
        SearchOperation searchOperation = SearchOperation.getOperation(itemSearchCriteria.getOperation());
        String attributeName = itemSearchCriteria.getFilterKey();

        if (!isSearchAttributePermitted(attributeName)) {
            log.error("{} : Invalid Search Field", attributeName);
            throw new InvalidOperationException(
                    String.format("%s : Invalid Search Field", attributeName)
            );
        }

        switch (searchOperation) {
            case LIKE -> {
                if (attributeName.equals("price")) {
                    return criteriaBuilder.equal(
                            root.get(attributeName),
                            Integer.parseInt(valueToSearch)
                    );
                }

                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(attributeName)),
                        "%" + valueToSearch + "%"
                );
            }

            case LESS_THAN -> {
                if(attributeName.equals("price")) {
                    return criteriaBuilder.lessThanOrEqualTo(
                            root.get(attributeName),
                            Integer.parseInt(valueToSearch)
                    );
                }

                return criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder.lower(root.get(attributeName)),
                        "%" + valueToSearch + "%"
                );
            }

            case GREATER_THAN -> {
                if(attributeName.equals("price")) {
                    return criteriaBuilder.greaterThanOrEqualTo(
                            root.get(attributeName),
                            Integer.parseInt(valueToSearch)
                    );
                }

                return criteriaBuilder.greaterThanOrEqualTo(
                        criteriaBuilder.lower(root.get(attributeName)),
                        "%" + valueToSearch + "%"
                );
            }
        }
        return null;
    }
}
