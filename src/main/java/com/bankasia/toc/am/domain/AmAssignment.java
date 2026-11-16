package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "am_assignment",
       uniqueConstraints = @UniqueConstraint(columnNames = {"duty_date", "slot", "member_id"}),
       indexes = @Index(name = "idx_am_assign_date", columnList = "duty_date"))
@Getter @Setter @NoArgsConstructor
public class AmAssignment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duty_date", nullable = false)
    private LocalDate dutyDate;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 15)
    private AmSlot slot;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private AmMember member;

    public AmAssignment(LocalDate dutyDate, AmSlot slot, AmMember member) {
        this.dutyDate = dutyDate;
        this.slot = slot;
        this.member = member;
    }
}
