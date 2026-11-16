package com.bankasia.toc.am.web;

import com.bankasia.toc.am.domain.AmSlot;
import com.bankasia.toc.am.domain.DayType;
import com.bankasia.toc.am.repository.AmEscalationRepository;
import com.bankasia.toc.am.service.AmMemberService;
import com.bankasia.toc.am.service.AmRosterService;
import com.bankasia.toc.am.service.AmRosterService.AmDayRow;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/am")
public class AmRosterController {

    private final AmRosterService roster;
    private final AmMemberService members;
    private final AmEscalationRepository escalations;

    public AmRosterController(AmRosterService roster, AmMemberService members, AmEscalationRepository escalations) {
        this.roster = roster;
        this.members = members;
        this.escalations = escalations;
    }

    @GetMapping
    public String home() { return "redirect:/am/roster"; }

    @GetMapping("/roster")
    public String month(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                        Model model) {
        YearMonth ym = month != null ? month : roster.currentMonth();
        model.addAttribute("month", ym);
        model.addAttribute("prev", ym.minusMonths(1));
        model.addAttribute("next", ym.plusMonths(1));
        model.addAttribute("rows", roster.monthRoster(ym));
        model.addAttribute("slots", AmSlot.values());
        model.addAttribute("onDuty", roster.onDutyNow());
        model.addAttribute("today", roster.today());
        model.addAttribute("escalations", escalations.findAllByOrderByLevelAsc());
        return "am-roster";
    }

    @GetMapping("/roster/{date}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        model.addAttribute("date", date);
        model.addAttribute("form", AmDayForm.from(roster.day(date)));
        model.addAttribute("staff", members.findActive());
        model.addAttribute("slots", AmSlot.values());
        model.addAttribute("dayTypes", DayType.values());
        return "am-roster-edit";
    }

    @PostMapping("/roster/{date}")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @ModelAttribute("form") AmDayForm form, RedirectAttributes ra) {
        roster.saveDay(date, form.getDayType(), form.toMap());
        ra.addFlashAttribute("message", "Roster for " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy")) + " saved.");
        return "redirect:/am/roster?month=" + YearMonth.from(date);
    }

    @GetMapping("/roster/export")
    public ResponseEntity<byte[]> export(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        StringBuilder sb = new StringBuilder("\uFEFFSL,Date,Day,Roster Duty,Additional Duty,Replacement During Roster Duty\n");
        int sl = 1;
        for (AmDayRow r : roster.monthRoster(month)) {
            sb.append(sl++).append(',').append(r.date().format(DateTimeFormatter.ofPattern("d-MMM-yyyy")))
              .append(',').append(r.getDayOfWeek());
            switch (r.type()) {
                case HOLIDAY -> sb.append(",HOLIDAY,,");
                case ALL_PRESENT -> sb.append(",ALL TEAM MEMBERS PRESENT,,");
                case SATURDAY_DUTY -> sb.append(",\"").append(r.getRosterText()).append("\",,");
                default -> sb.append(",\"").append(r.getRosterText()).append("\",\"").append(r.getAdditionalText())
                        .append("\",\"").append(r.getReplacementText()).append('"');
            }
            sb.append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"am-roster-" + month + ".csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
