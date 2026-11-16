package com.bankasia.toc.am.web;

import com.bankasia.toc.am.service.AmMemberService;
import com.bankasia.toc.am.service.DepartmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/am/departments")
public class AmDepartmentController {

    private final DepartmentService departments;
    private final AmMemberService members;

    public AmDepartmentController(DepartmentService departments, AmMemberService members) {
        this.departments = departments;
        this.members = members;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("officers", departments.grouped());
        model.addAttribute("staff", members.findActive());
        return "am-departments";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String add(@RequestParam String name, @RequestParam Long memberId, RedirectAttributes ra) {
        departments.add(name, memberId);
        ra.addFlashAttribute("message", "Department assigned.");
        return "redirect:/am/departments";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        departments.delete(id);
        ra.addFlashAttribute("message", "Department removed.");
        return "redirect:/am/departments";
    }
}
