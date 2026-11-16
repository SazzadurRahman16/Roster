package com.bankasia.toc.am.web;

import com.bankasia.toc.am.domain.*;
import com.bankasia.toc.am.service.AmRosterService.AmDayRow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class AmDayForm {
    private DayType dayType = DayType.WORKING;
    private List<Long> roster = new ArrayList<>();
    private List<Long> additional = new ArrayList<>();
    private List<Long> replacement = new ArrayList<>();

    public static AmDayForm from(AmDayRow row) {
        AmDayForm f = new AmDayForm();
        f.dayType = row.type();
        f.roster = ids(row.slots().get(AmSlot.ROSTER));
        f.additional = ids(row.slots().get(AmSlot.ADDITIONAL));
        f.replacement = ids(row.slots().get(AmSlot.REPLACEMENT));
        return f;
    }

    public Map<AmSlot, List<Long>> toMap() {
        Map<AmSlot, List<Long>> m = new EnumMap<>(AmSlot.class);
        m.put(AmSlot.ROSTER, roster);
        m.put(AmSlot.ADDITIONAL, additional);
        m.put(AmSlot.REPLACEMENT, replacement);
        return m;
    }

    private static List<Long> ids(List<AmMember> l) { return new ArrayList<>(l.stream().map(AmMember::getId).toList()); }
}
