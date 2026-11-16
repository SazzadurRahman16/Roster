package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Day-level information. A date without a row is a normal working day. */
@Entity
@Table(name = "am_day", uniqueConstraints = @UniqueConstraint(columnNames = "duty_date"))
@Getter @Setter @NoArgsConstructor
public class AmDay {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duty_date", nullable = false)
    private LocalDate dutyDate;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private DayType dayType = DayType.WORKING;

    public AmDay(LocalDate dutyDate, DayType dayType) {
        this.dutyDate = dutyDate;
        this.dayType = dayType;
    }
}
