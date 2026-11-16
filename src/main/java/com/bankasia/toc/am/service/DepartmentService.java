package com.bankasia.toc.am.service;

import com.bankasia.toc.am.domain.AmDepartment;
import com.bankasia.toc.am.domain.AmMember;
import com.bankasia.toc.am.repository.AmDepartmentRepository;
import com.bankasia.toc.am.repository.AmMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DepartmentService {

    public record OfficerDepartments(AmMember member, List<AmDepartment> departments) {
        public AmMember getMember() { return member; }
        public List<AmDepartment> getDepartments() { return departments; }
    }

    private final AmDepartmentRepository departments;
    private final AmMemberRepository members;

    public DepartmentService(AmDepartmentRepository departments, AmMemberRepository members) {
        this.departments = departments;
        this.members = members;
    }

    /** Departments grouped by assigned officer, in the order officers first appear. */
    @Transactional(readOnly = true)
    public List<OfficerDepartments> grouped() {
        Map<Long, List<AmDepartment>> map = new LinkedHashMap<>();
        Map<Long, AmMember> who = new HashMap<>();
        for (AmDepartment d : departments.findAllByOrderByIdAsc()) {
            map.computeIfAbsent(d.getMember().getId(), k -> new ArrayList<>()).add(d);
            who.put(d.getMember().getId(), d.getMember());
        }
        return map.entrySet().stream().map(e -> new OfficerDepartments(who.get(e.getKey()), e.getValue())).toList();
    }

    @Transactional
    public void add(String name, Long memberId) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Department name is required");
        AmMember m = members.findById(memberId).orElseThrow(() -> new NoSuchElementException("Officer not found"));
        departments.save(new AmDepartment(name.trim(), m));
    }

    @Transactional
    public void delete(Long id) { departments.deleteById(id); }
}
