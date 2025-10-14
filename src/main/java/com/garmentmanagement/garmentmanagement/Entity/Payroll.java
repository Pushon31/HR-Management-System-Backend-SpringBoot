package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payrolls")
public class Payroll extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period", nullable = false)
    private YearMonth payPeriod;

    @Column(name = "pay_date")
    private LocalDate payDate;

    // Earnings (Simplified)
    @Column(name = "basic_salary", precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "total_allowances", precision = 10, scale = 2)
    private BigDecimal totalAllowances;

    @Column(name = "overtime_pay", precision = 10, scale = 2)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "bonus", precision = 10, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    // Deductions (Simplified)
    @Column(name = "tax_deduction", precision = 10, scale = 2)
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    @Column(name = "other_deductions", precision = 10, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    // Calculated Fields
    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary;

    // Attendance Summary
    @Column(name = "working_days")
    private Integer workingDays;

    @Column(name = "present_days")
    private Integer presentDays;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PayrollStatus status = PayrollStatus.PENDING;

    @Column(length = 500)
    private String remarks;

    public enum PayrollStatus {
        PENDING, PROCESSED, PAID, CANCELLED
    }

    @PrePersist
    @PreUpdate
    public void calculatePayroll() {
        // Calculate gross salary
        this.grossSalary = basicSalary
                .add(totalAllowances)
                .add(overtimePay)
                .add(bonus);

        // Calculate net salary
        this.netSalary = grossSalary
                .subtract(taxDeduction)
                .subtract(otherDeductions);
    }
}