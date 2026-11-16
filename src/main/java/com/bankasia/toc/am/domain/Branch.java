package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The same branch code may appear in more than one group (e.g. 108 Agent Banking Back Office). */
@Entity
@Table(name = "am_branch", indexes = @Index(name = "idx_am_branch_code", columnList = "code"))
@Getter @Setter @NoArgsConstructor
public class Branch {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private BranchGroup group;

    public Branch(String code, String name, BranchGroup group) {
        this.code = code;
        this.name = name;
        this.group = group;
    }
}
