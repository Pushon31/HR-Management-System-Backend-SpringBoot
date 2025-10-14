// DTO: ApplicationDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long id;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private Long jobPostingId;
    private String jobTitle;
    private String jobCode;
    private String departmentName;
    private LocalDateTime applicationDate;
    private String status;
    private String coverLetter;
    private BigDecimal expectedSalary;
    private Integer noticePeriod;
    private String notes;
    private String rejectionReason;

    // Interview info
    private Boolean hasInterview;
    private LocalDateTime interviewDate;
}