package com.bankasia.toc.am.domain;

public enum DayType {
    WORKING("Working day"),
    HOLIDAY("Holiday"),
    SATURDAY_DUTY("Saturday duty (single officer)"),
    ALL_PRESENT("All team members present (e.g. month end)");

    private final String label;
    DayType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
