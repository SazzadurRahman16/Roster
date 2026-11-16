package com.bankasia.toc.am.repository;

import com.bankasia.toc.am.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findAllByOrderByIdAsc();

    @Query("select b from Branch b where lower(b.name) like lower(concat('%', :q, '%')) "
         + "or b.code like concat('%', :q, '%') order by b.code, b.id")
    List<Branch> search(@Param("q") String q);
}
