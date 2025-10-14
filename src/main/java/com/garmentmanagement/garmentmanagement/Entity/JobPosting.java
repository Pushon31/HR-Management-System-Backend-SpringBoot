// Entity: JobPosting.java
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
@Table(name = "job_postings")
public class JobPosting extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String jobTitle;

    @Column(nullable = false, length = 50)
    private String jobCode; // HR-JOB-001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EmploymentType employmentType;

    @Column(length = 50)
    private String experienceLevel; // Entry, Mid, Senior

    @Column(precision = 12, scale = 2)
    private BigDecimal minSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal maxSalary;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "vacancies")
    private Integer vacancies = 1;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private JobStatus status = JobStatus.OPEN;

    @Column(name = "posted_date")
    private LocalDate postedDate;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    public enum EmploymentType {
        FULL_TIME, PART_TIME, CONTRACT, INTERN
    }

    public enum JobStatus {
        DRAFT, OPEN, CLOSED, ON_HOLD, CANCELLED
    }
}