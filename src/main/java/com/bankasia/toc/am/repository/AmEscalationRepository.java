package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.AmEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmEscalationRepository extends JpaRepository<AmEscalation, Long> {
    List<AmEscalation> findAllByOrderByLevelAsc();
}
