package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "salary_structures")
public class SalaryStructure extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    // Core Salary Components (শুধু essential fields রাখব)
    @Column(name = "basic_salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "house_rent", precision = 10, scale = 2)
    private BigDecimal houseRent = BigDecimal.ZERO;

    @Column(name = "medical_allowance", precision = 10, scale = 2)
    private BigDecimal medicalAllowance = BigDecimal.ZERO;

    @Column(name = "transport_allowance", precision = 10, scale = 2)
    private BigDecimal transportAllowance = BigDecimal.ZERO;

    // Consolidated Fields (একসাথে রাখব)
    @Column(name = "other_allowances", precision = 10, scale = 2)
    private BigDecimal otherAllowances = BigDecimal.ZERO;

    @Column(name = "total_deductions", precision = 10, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    // Calculated Fields
    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SalaryStatus status = SalaryStatus.ACTIVE;

    public enum SalaryStatus {
        ACTIVE, INACTIVE
    }

    @PrePersist
    @PreUpdate
    public void calculateSalaries() {
        // Calculate gross salary
        this.grossSalary = basicSalary
                .add(houseRent)
                .add(medicalAllowance)
                .add(transportAllowance)
                .add(otherAllowances);

        // Calculate net salary
        this.netSalary = grossSalary.subtract(totalDeductions);
    }
}