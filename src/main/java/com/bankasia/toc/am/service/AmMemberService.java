package com.bankasia.toc.am.service;

import com.bankasia.toc.am.domain.AmMember;
import com.bankasia.toc.am.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AmMemberService {

    private final AmMemberRepository members;
    private final AmAssignmentRepository assignments;
    private final BranchGroupRepository groups;
    private final AmDepartmentRepository departments;

    public AmMemberService(AmMemberRepository members, AmAssignmentRepository assignments,
                           BranchGroupRepository groups, AmDepartmentRepository departments) {
        this.members = members;
        this.assignments = assignments;
        this.groups = groups;
        this.departments = departments;
    }

    public List<AmMember> findAll() { return members.findAllByOrderByIdAsc(); }
    public List<AmMember> findActive() { return members.findByActiveTrueOrderByIdAsc(); }

    public AmMember get(Long id) {
        return members.findById(id).orElseThrow(() -> new NoSuchElementException("Member " + id + " not found"));
    }

    @Transactional
    public AmMember save(AmMember m) {
        if (m.getMobile() != null && m.getMobile().isBlank()) m.setMobile(null);
        if (m.getIpExt() != null && m.getIpExt().isBlank()) m.setIpExt(null);
        if (m.getMobile() != null) {
            boolean dup = m.getId() == null ? members.existsByMobile(m.getMobile())
                                            : members.existsByMobileAndIdNot(m.getMobile(), m.getId());
            if (dup) throw new IllegalArgumentException("Another member already uses this mobile number");
        }
        return members.save(m);
    }

    @Transactional
    public void setActive(Long id, boolean active) { get(id).setActive(active); }

    /** Members referenced by rosters, branch groups or departments are deactivated instead of deleted. */
    @Transactional
    public void remove(Long id) {
        AmMember m = get(id);
        boolean used = assignments.existsByMemberId(id) || groups.existsByPrimaryIdOrSecondaryId(id, id)
                || departments.existsByMemberId(id);
        if (used) m.setActive(false); else members.delete(m);
    }
}
