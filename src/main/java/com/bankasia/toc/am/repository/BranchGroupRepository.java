package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.BranchGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchGroupRepository extends JpaRepository<BranchGroup, Long> {
    List<BranchGroup> findAllByOrderByIdAsc();
    boolean existsByPrimaryIdOrSecondaryId(Long primaryId, Long secondaryId);
}
