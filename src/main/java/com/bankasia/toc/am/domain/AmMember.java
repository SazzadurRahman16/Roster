package com.bankasia.toc.am.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "am_member")
@Getter @Setter @NoArgsConstructor
public class AmMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false, length = 120)
    private String name;

    /** Optional: some officers have no mobile number on the published list. */
    @Pattern(regexp = "|01\\d{9}", message = "Enter an 11-digit mobile number starting with 01")
    @Column(unique = true, length = 20)
    private String mobile;

    @Column(length = 20)
    private String ipExt;

    /** Shown as "(ERA)" after the name in the roster. */
    private boolean era;

    private boolean active = true;

    public AmMember(String name, String mobile, String ipExt, boolean era) {
        this.name = name;
        this.mobile = mobile;
        this.ipExt = ipExt;
        this.era = era;
    }

    public String getDisplayName() { return era ? name + " (ERA)" : name; }
}
