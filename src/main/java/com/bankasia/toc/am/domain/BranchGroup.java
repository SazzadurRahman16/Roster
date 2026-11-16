package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A set of branches served by one primary and (optionally) one secondary officer. */
@Entity
@Table(name = "am_branch_group")
@Getter @Setter @NoArgsConstructor
public class BranchGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String label;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_id")
    private AmMember primary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "secondary_id")
    private AmMember secondary;

    public BranchGroup(String label, AmMember primary, AmMember secondary) {
        this.label = label;
        this.primary = primary;
        this.secondary = secondary;
    }
}
