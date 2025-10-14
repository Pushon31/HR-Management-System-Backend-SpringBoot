// Entity: Payslip.java
package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payslips")
public class Payslip extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @Column(name = "payslip_code", unique = true, nullable = false, length = 20)
    private String payslipCode; // PS-202410-001

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "is_generated")
    private Boolean isGenerated = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PayslipStatus status = PayslipStatus.GENERATED;

    public enum PayslipStatus {
        GENERATED, DOWNLOADED, ARCHIVED
    }
}