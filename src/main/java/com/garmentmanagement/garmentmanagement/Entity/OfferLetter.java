// Entity: OfferLetter.java
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
@Table(name = "offer_letters")
public class OfferLetter extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "offer_code", unique = true, nullable = false, length = 50)
    private String offerCode; // OFFER-2024-001

    @Column(name = "offer_date", nullable = false)
    private LocalDate offerDate;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "basic_salary", precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "house_rent", precision = 10, scale = 2)
    private BigDecimal houseRent;

    @Column(name = "medical_allowance", precision = 10, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(name = "other_allowances", precision = 10, scale = 2)
    private BigDecimal otherAllowances;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "probation_period")
    private Integer probationPeriod; // In months

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OfferStatus status = OfferStatus.PENDING;

    @Column(name = "response_date")
    private LocalDate responseDate;

    @Column(name = "response_notes", length = 500)
    private String responseNotes;

    public enum OfferStatus {
        PENDING, ACCEPTED, REJECTED, EXPIRED
    }

    @PrePersist
    @PreUpdate
    public void calculateGrossSalary() {
        this.grossSalary = basicSalary
                .add(houseRent)
                .add(medicalAllowance)
                .add(otherAllowances);
    }
}