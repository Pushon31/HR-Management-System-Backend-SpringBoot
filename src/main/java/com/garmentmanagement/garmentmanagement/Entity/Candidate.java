// Entity: Candidate.java
package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "candidates")
public class Candidate extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(unique = true, length = 30)
    private String nidNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate birthDate;

    @Column(length = 100)
    private String currentAddress;

    @Column(length = 100)
    private String permanentAddress;

    @Column(length = 100)
    private String currentJobTitle;

    @Column(length = 100)
    private String currentCompany;

    @Column(length = 50)
    private String experience;

    @Column(length = 100)
    private String education;

    @Column(length = 100)
    private String skills;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "expected_salary", precision = 12, scale = 2)
    private BigDecimal expectedSalary;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CandidateStatus status = CandidateStatus.NEW;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum CandidateStatus {
        NEW, CONTACTED, SHORTLISTED, REJECTED, HIRED
    }
}