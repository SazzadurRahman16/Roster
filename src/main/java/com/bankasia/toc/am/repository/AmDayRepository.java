package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.AmDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AmDayRepository extends JpaRepository<AmDay, Long> {
    List<AmDay> findByDutyDateBetween(LocalDate from, LocalDate to);
    Optional<AmDay> findByDutyDate(LocalDate date);
}
