package com.bankasia.toc.am.service;

import com.bankasia.toc.am.domain.*;
import com.bankasia.toc.am.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BranchService {

    public record GroupView(BranchGroup group, List<Branch> branches) {
        public BranchGroup getGroup() { return group; }
        public List<Branch> getBranches() { return branches; }
    }

    private final BranchGroupRepository groups;
    private final BranchRepository branches;
    private final AmMemberRepository members;

    public BranchService(BranchGroupRepository groups, BranchRepository branches, AmMemberRepository members) {
        this.groups = groups;
        this.branches = branches;
        this.members = members;
    }

    @Transactional(readOnly = true)
    public List<GroupView> groupViews() {
        Map<Long, List<Branch>> byGroup = branches.findAllByOrderByIdAsc().stream()
                .collect(Collectors.groupingBy(b -> b.getGroup().getId()));
        return groups.findAllByOrderByIdAsc().stream()
                .map(g -> new GroupView(g, byGroup.getOrDefault(g.getId(), List.of()))).toList();
    }

    @Transactional(readOnly = true)
    public GroupView groupView(Long id) {
        BranchGroup g = groups.findById(id).orElseThrow(() -> new NoSuchElementException("Group " + id + " not found"));
        return new GroupView(g, branches.findAllByOrderByIdAsc().stream()
                .filter(b -> b.getGroup().getId().equals(id)).toList());
    }

    @Transactional(readOnly = true)
    public List<Branch> search(String q) { return branches.search(q.trim()); }

    @Transactional
    public void updateOfficers(Long groupId, Long primaryId, Long secondaryId) {
        BranchGroup g = groups.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        if (primaryId == null) throw new IllegalArgumentException("A primary officer is required");
        if (primaryId.equals(secondaryId)) throw new IllegalArgumentException("Primary and secondary must differ");
        g.setPrimary(members.findById(primaryId).orElseThrow(() -> new NoSuchElementException("Officer not found")));
        g.setSecondary(secondaryId == null ? null : members.findById(secondaryId)
                .orElseThrow(() -> new NoSuchElementException("Officer not found")));
    }

    @Transactional
    public void addBranch(Long groupId, String code, String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Branch name is required");
        BranchGroup g = groups.findById(groupId).orElseThrow(() -> new NoSuchElementException("Group not found"));
        branches.save(new Branch(code == null || code.isBlank() ? null : code.trim(), name.trim().toUpperCase(), g));
    }

    /** @return id of the group the branch belonged to. */
    @Transactional
    public Long deleteBranch(Long branchId) {
        Branch b = branches.findById(branchId).orElseThrow(() -> new NoSuchElementException("Branch not found"));
        Long groupId = b.getGroup().getId();
        branches.delete(b);
        return groupId;
    }
}
