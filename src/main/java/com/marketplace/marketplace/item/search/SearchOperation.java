package com.marketplace.marketplace.item.search;

public enum SearchOperation {
    LESS_THAN,
    LIKE,
    GREATER_THAN;

    private final String[] searchAbbreviation = {
            "ls",
            "lk",
            "gt"
    };

    public static SearchOperation getOperation(String input) {
        return switch (input) {
            case "lt" -> LESS_THAN;
            case "lk" -> LIKE;
            case "gt" -> GREATER_THAN;
            default -> null;
        };
    }
}
