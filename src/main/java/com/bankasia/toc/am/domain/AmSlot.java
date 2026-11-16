package com.bankasia.toc.am.domain;

import java.time.LocalTime;

/** Duty slots of the Application Management Team roster. */
public enum AmSlot {
    ROSTER("Roster Duty", LocalTime.of(14, 0), LocalTime.of(21, 0)),
    ADDITIONAL("Additional Duty", LocalTime.of(18, 0), LocalTime.of(18, 30)),
    REPLACEMENT("Replacement During Roster Duty", LocalTime.of(10, 0), LocalTime.of(14, 0));

    private final String label;
    private final LocalTime start;
    private final LocalTime end;

    AmSlot(String label, LocalTime start, LocalTime end) {
        this.label = label;
        this.start = start;
        this.end = end;
    }

    public String getLabel() { return label; }
    public boolean covers(LocalTime t) { return !t.isBefore(start) && t.isBefore(end); }
    public String getTimeRange() { return fmt(start) + " - " + fmt(end); }

    private static String fmt(LocalTime t) {
        int h = t.getHour() % 12 == 0 ? 12 : t.getHour() % 12;
        return String.format("%02d:%02d %s", h, t.getMinute(), t.getHour() < 12 ? "AM" : "PM");
    }
}
