package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.AmAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AmAssignmentRepository extends JpaRepository<AmAssignment, Long> {

    List<AmAssignment> findByDutyDateBetweenOrderByDutyDateAscMemberIdAsc(LocalDate from, LocalDate to);

    List<AmAssignment> findByDutyDateOrderByMemberIdAsc(LocalDate date);

    boolean existsByMemberId(Long memberId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from AmAssignment a where a.dutyDate = :date")
    void deleteDay(@Param("date") LocalDate date);
}
