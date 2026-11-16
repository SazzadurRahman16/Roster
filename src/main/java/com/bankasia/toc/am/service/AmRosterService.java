package com.bankasia.toc.am.service;

import com.bankasia.toc.am.domain.*;
import com.bankasia.toc.am.repository.AmAssignmentRepository;
import com.bankasia.toc.am.repository.AmDayRepository;
import com.bankasia.toc.am.repository.AmMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AmRosterService {

    /** One row of the monthly roster. */
    public record AmDayRow(LocalDate date, DayType type, Map<AmSlot, List<AmMember>> slots) {
        public LocalDate getDate() { return date; }
        public DayType getType() { return type; }
        public Map<AmSlot, List<AmMember>> getSlots() { return slots; }
        public DayOfWeek getDayOfWeek() { return date.getDayOfWeek(); }

        public String text(AmSlot s) {
            return slots.get(s).stream().map(AmMember::getDisplayName).collect(Collectors.joining(" & "));
        }
        public String getRosterText() { return text(AmSlot.ROSTER); }
        public String getAdditionalText() { return text(AmSlot.ADDITIONAL); }
        public String getReplacementText() { return text(AmSlot.REPLACEMENT); }

        /** CSS class used by the roster table. */
        public String getCssClass() {
            return switch (type) {
                case HOLIDAY -> "off";
                case SATURDAY_DUTY -> "sat";
                case ALL_PRESENT -> "allin";
                default -> "";
            };
        }
    }

    /** Who is on duty at a moment in time. */
    public record OnDuty(DayType type, Map<AmSlot, List<AmMember>> slots) {
        public DayType getType() { return type; }
        public Map<AmSlot, List<AmMember>> getSlots() { return slots; }
    }

    private final AmAssignmentRepository assignments;
    private final AmDayRepository days;
    private final AmMemberRepository members;
    private final ZoneId zone;

    public AmRosterService(AmAssignmentRepository assignments, AmDayRepository days, AmMemberRepository members,
                           @Value("${app.zone:Asia/Dhaka}") String zone) {
        this.assignments = assignments;
        this.days = days;
        this.members = members;
        this.zone = ZoneId.of(zone);
    }

    public LocalDate today() { return LocalDate.now(zone); }
    public YearMonth currentMonth() { return YearMonth.now(zone); }

    @Transactional(readOnly = true)
    public List<AmDayRow> monthRoster(YearMonth month) {
        Map<LocalDate, DayType> types = days.findByDutyDateBetween(month.atDay(1), month.atEndOfMonth())
                .stream().collect(Collectors.toMap(AmDay::getDutyDate, AmDay::getDayType));
        Map<LocalDate, List<AmAssignment>> byDate = assignments
                .findByDutyDateBetweenOrderByDutyDateAscMemberIdAsc(month.atDay(1), month.atEndOfMonth())
                .stream().collect(Collectors.groupingBy(AmAssignment::getDutyDate));

        List<AmDayRow> rows = new ArrayList<>();
        for (int d = 1; d <= month.lengthOfMonth(); d++) {
            LocalDate date = month.atDay(d);
            rows.add(new AmDayRow(date, types.getOrDefault(date, DayType.WORKING),
                    group(byDate.getOrDefault(date, List.of()))));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public AmDayRow day(LocalDate date) {
        DayType type = days.findByDutyDate(date).map(AmDay::getDayType).orElse(DayType.WORKING);
        return new AmDayRow(date, type, group(assignments.findByDutyDateOrderByMemberIdAsc(date)));
    }

    /**
     * Saves one day. Holidays and "all present" days hold no assignments;
     * Saturday duty keeps only the single main slot.
     */
    @Transactional
    public void saveDay(LocalDate date, DayType type, Map<AmSlot, List<Long>> idsBySlot) {
        AmDay day = days.findByDutyDate(date).orElseGet(() -> new AmDay(date, type));
        day.setDayType(type);
        days.save(day);

        assignments.deleteDay(date);
        if (type == DayType.HOLIDAY || type == DayType.ALL_PRESENT) return;

        Map<Long, AmMember> active = members.findByActiveTrueOrderByIdAsc().stream()
                .collect(Collectors.toMap(AmMember::getId, m -> m));
        for (AmSlot slot : AmSlot.values()) {
            if (type == DayType.SATURDAY_DUTY && slot != AmSlot.ROSTER) continue;
            for (Long id : new LinkedHashSet<>(Optional.ofNullable(idsBySlot.get(slot)).orElse(List.of()))) {
                AmMember m = active.get(id);
                if (m == null) throw new IllegalArgumentException("Member " + id + " is not active");
                assignments.save(new AmAssignment(date, slot, m));
            }
        }
    }

    @Transactional(readOnly = true)
    public OnDuty onDutyNow() { return onDutyAt(LocalDateTime.now(zone)); }

    @Transactional(readOnly = true)
    public OnDuty onDutyAt(LocalDateTime now) {
        AmDayRow row = day(now.toLocalDate());
        Map<AmSlot, List<AmMember>> result = new EnumMap<>(AmSlot.class);
        switch (row.type()) {
            case WORKING -> {
                for (AmSlot s : AmSlot.values())
                    if (s.covers(now.toLocalTime())) result.put(s, row.slots().get(s));
            }
            case SATURDAY_DUTY -> result.put(AmSlot.ROSTER, row.slots().get(AmSlot.ROSTER));
            default -> { }
        }
        return new OnDuty(row.type(), result);
    }

    private static Map<AmSlot, List<AmMember>> group(List<AmAssignment> list) {
        Map<AmSlot, List<AmMember>> map = new EnumMap<>(AmSlot.class);
        for (AmSlot s : AmSlot.values()) map.put(s, new ArrayList<>());
        list.forEach(a -> map.get(a.getSlot()).add(a.getMember()));
        return map;
    }
}
