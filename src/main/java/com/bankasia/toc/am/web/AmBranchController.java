package com.bankasia.toc.am.web;

import com.bankasia.toc.am.service.AmMemberService;
import com.bankasia.toc.am.service.BranchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/am/branches")
public class AmBranchController {

    private final BranchService branches;
    private final AmMemberService members;

    public AmBranchController(BranchService branches, AmMemberService members) {
        this.branches = branches;
        this.members = members;
    }

    /** Public: browse all groups, or search by branch code / name. */
    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("q", q == null ? "" : q);
        if (q != null && !q.isBlank()) model.addAttribute("results", branches.search(q));
        else model.addAttribute("groups", branches.groupViews());
        return "am-branches";
    }

    @GetMapping("/groups/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("view", branches.groupView(id));
        model.addAttribute("staff", members.findActive());
        return "am-group-edit";
    }

    @PostMapping("/groups/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveOfficers(@PathVariable Long id, @RequestParam Long primaryId,
                               @RequestParam(required = false) Long secondaryId, RedirectAttributes ra) {
        branches.updateOfficers(id, primaryId, secondaryId);
        ra.addFlashAttribute("message", "Assigned officers updated.");
        return "redirect:/am/branches/groups/" + id + "/edit";
    }

    @PostMapping("/groups/{id}/branches")
    @PreAuthorize("hasRole('ADMIN')")
    public String addBranch(@PathVariable Long id, @RequestParam(required = false) String code,
                            @RequestParam String name, RedirectAttributes ra) {
        branches.addBranch(id, code, name);
        ra.addFlashAttribute("message", "Branch added.");
        return "redirect:/am/branches/groups/" + id + "/edit";
    }

    @PostMapping("/branch/{branchId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBranch(@PathVariable Long branchId, RedirectAttributes ra) {
        Long groupId = branches.deleteBranch(branchId);
        ra.addFlashAttribute("message", "Branch removed.");
        return "redirect:/am/branches/groups/" + groupId + "/edit";
    }
}
