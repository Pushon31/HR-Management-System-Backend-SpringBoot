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
@Table(name = "leave_types")
public class LeaveType extends BaseEntity {

    public enum LeaveCategory {
        PAID, UNPAID, SICK, MATERNITY, PATERNITY, SPECIAL
    }

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String code; // SL, CL, AL, ML, PL, etc.

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LeaveCategory category;

    @Column(length = 255)
    private String description;

    @Column(name = "max_days_per_year")
    private Integer maxDaysPerYear;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = true;

    @Column(name = "allow_encashment")
    private Boolean allowEncashment = false;

    @Column(name = "carry_forward_days")
    private Integer carryForwardDays = 0;
}