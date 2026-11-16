package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.AmDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmDepartmentRepository extends JpaRepository<AmDepartment, Long> {
    List<AmDepartment> findAllByOrderByIdAsc();
    boolean existsByMemberId(Long memberId);
}
