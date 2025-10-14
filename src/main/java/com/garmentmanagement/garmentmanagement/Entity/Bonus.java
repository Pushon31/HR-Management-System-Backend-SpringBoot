// Entity: Bonus.java
package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bonuses")
public class Bonus extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "bonus_type", length = 50)
    private String bonusType; // Festival, Performance, Annual

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "bonus_date")
    private LocalDate bonusDate;

    @Column(length = 200)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BonusStatus status = BonusStatus.APPROVED;

    public enum BonusStatus {
        PENDING, APPROVED, REJECTED, PAID
    }
}