package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A corporate office department/division and the officer assigned to it. */
@Entity
@Table(name = "am_department")
@Getter @Setter @NoArgsConstructor
public class AmDepartment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private AmMember member;

    public AmDepartment(String name, AmMember member) {
        this.name = name;
        this.member = member;
    }
}
