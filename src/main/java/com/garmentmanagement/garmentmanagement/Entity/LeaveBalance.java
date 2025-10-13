package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_balances")
public class LeaveBalance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "total_days")
    private Integer totalDays = 0;

    @Column(name = "used_days")
    private Integer usedDays = 0;

    @Column(name = "remaining_days")
    private Integer remainingDays = 0;

    @Column(name = "carry_forward_days")
    private Integer carryForwardDays = 0;

    @Column(name = "year")
    private Integer year; // Financial year or calendar year

    // Pre-persist and pre-update to calculate remaining days
    @PrePersist
    @PreUpdate
    public void calculateRemainingDays() {
        this.remainingDays = this.totalDays - this.usedDays + this.carryForwardDays;
    }
}