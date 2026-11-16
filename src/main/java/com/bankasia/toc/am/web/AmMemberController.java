package com.bankasia.toc.am.web;

import com.bankasia.toc.am.domain.AmMember;
import com.bankasia.toc.am.service.AmMemberService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/am/team")
public class AmMemberController {

    private final AmMemberService service;

    public AmMemberController(AmMemberService service) { this.service = service; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", service.findAll());
        return "am-team";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(Model model) {
        model.addAttribute("member", new AmMember());
        return "am-member-form";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("member", service.get(id));
        return "am-member-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute("member") AmMember member, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "am-member-form";
        try {
            service.save(member);
        } catch (IllegalArgumentException ex) {
            result.rejectValue("mobile", "duplicate", ex.getMessage());
            return "am-member-form";
        }
        ra.addFlashAttribute("message", member.getName() + " saved.");
        return "redirect:/am/team";
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public String setActive(@PathVariable Long id, @RequestParam boolean value, RedirectAttributes ra) {
        service.setActive(id, value);
        ra.addFlashAttribute("message", value ? "Member reactivated." : "Member deactivated.");
        return "redirect:/am/team";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.remove(id);
        ra.addFlashAttribute("message", "Member removed (deactivated if still referenced by rosters, branches or departments).");
        return "redirect:/am/team";
    }
}
