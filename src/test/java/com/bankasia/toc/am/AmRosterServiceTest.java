package com.bankasia.toc.am;

import com.bankasia.toc.am.domain.*;
import com.bankasia.toc.am.service.AmRosterService;
import com.bankasia.toc.am.service.AmRosterService.AmDayRow;
import com.bankasia.toc.am.service.BranchService;
import com.bankasia.toc.am.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:amTest")
@Transactional
class AmRosterServiceTest {

    @Autowired AmRosterService roster;
    @Autowired BranchService branches;
    @Autowired DepartmentService departments;

    @Test
    void novemberRosterMatchesPdf() {
        List<AmDayRow> rows = roster.monthRoster(YearMonth.of(2026, 11));
        assertEquals(30, rows.size());

        AmDayRow nov1 = rows.get(0);
        assertEquals("Samia Ahmed & Md. Sahin Miah (ERA)", nov1.getRosterText());
        assertEquals("Masuma Sultana", nov1.getAdditionalText());
        assertEquals("Masuma Sultana & Sayeda Humayrha Mushrat", nov1.getReplacementText());

        assertEquals(DayType.HOLIDAY, rows.get(5).type());          // 6-Nov Friday
        assertEquals(DayType.SATURDAY_DUTY, rows.get(6).type());    // 7-Nov Saturday
        assertEquals("Sayeda Humayrha Mushrat", rows.get(6).getRosterText());
        assertEquals(DayType.ALL_PRESENT, rows.get(29).type());     // 30-Nov month end
    }

    @Test
    void onDutyFollowsSlotTimes() {
        // Mon 2-Nov 11:00 -> replacement slot only
        var morning = roster.onDutyAt(LocalDateTime.of(2026, 11, 2, 11, 0));
        assertEquals(List.of(AmSlot.REPLACEMENT), List.copyOf(morning.slots().keySet()));
        // Mon 2-Nov 18:15 -> roster and additional both running
        var evening = roster.onDutyAt(LocalDateTime.of(2026, 11, 2, 18, 15));
        assertTrue(evening.slots().containsKey(AmSlot.ROSTER));
        assertTrue(evening.slots().containsKey(AmSlot.ADDITIONAL));
        // Holiday -> nobody
        assertTrue(roster.onDutyAt(LocalDateTime.of(2026, 11, 6, 15, 0)).slots().isEmpty());
    }

    @Test
    void saveDayHandlesDayTypes() {
        LocalDate d = LocalDate.of(2026, 11, 2);
        Long id = roster.day(d).slots().get(AmSlot.ROSTER).get(0).getId();

        roster.saveDay(d, DayType.SATURDAY_DUTY, Map.of(AmSlot.ROSTER, List.of(id), AmSlot.ADDITIONAL, List.of(id)));
        AmDayRow sat = roster.day(d);
        assertEquals(1, sat.slots().get(AmSlot.ROSTER).size());
        assertTrue(sat.slots().get(AmSlot.ADDITIONAL).isEmpty());   // only main slot is kept

        roster.saveDay(d, DayType.HOLIDAY, Map.of(AmSlot.ROSTER, List.of(id)));
        assertTrue(roster.day(d).slots().get(AmSlot.ROSTER).isEmpty());
    }

    @Test
    void branchSearchAndGroups() {
        assertEquals(10, branches.groupViews().size());
        // code 108 is listed under two groups in the source PDF
        assertEquals(2, branches.search("108").size());
        var gulshan = branches.search("GULSHAN BRANCH").get(0);
        assertEquals("Md. Ashiqur Rahman", gulshan.getGroup().getPrimary().getName());
        assertEquals("Md. Zamiul Islam Talukdar", gulshan.getGroup().getSecondary().getName());
    }

    @Test
    void departmentsGroupedByOfficer() {
        var officers = departments.grouped();
        assertEquals(12, officers.size());
        assertEquals("Samia Ahmed", officers.get(0).getMember().getName());
        assertEquals(3, officers.get(0).getDepartments().size());
    }
}
