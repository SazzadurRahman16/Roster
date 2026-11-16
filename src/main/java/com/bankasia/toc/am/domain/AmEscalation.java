package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "am_escalation")
@Getter @Setter @NoArgsConstructor
public class AmEscalation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_no", nullable = false, unique = true)
    private int level;

    @Column(nullable = false) private String name;
    private String title;
    private String phone;

    public AmEscalation(int level, String name, String title, String phone) {
        this.level = level;
        this.name = name;
        this.title = title;
        this.phone = phone;
    }
}
