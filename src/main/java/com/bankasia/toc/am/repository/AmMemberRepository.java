package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.AmMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmMemberRepository extends JpaRepository<AmMember, Long> {
    List<AmMember> findAllByOrderByIdAsc();
    List<AmMember> findByActiveTrueOrderByIdAsc();
    boolean existsByMobile(String mobile);
    boolean existsByMobileAndIdNot(String mobile, Long id);
}
