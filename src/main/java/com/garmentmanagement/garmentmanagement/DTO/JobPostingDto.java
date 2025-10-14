// DTO: JobPostingDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDto {
    private Long id;
    private String jobTitle;
    private String jobCode;
    private Long departmentId;
    private String departmentName;
    private String employmentType;
    private String experienceLevel;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String jobDescription;
    private String requirements;
    private Integer vacancies;
    private LocalDate applicationDeadline;
    private String status;
    private LocalDate postedDate;

    // Statistics
    private Integer totalApplications;
    private Integer shortlistedCount;
    private Integer interviewedCount;
}