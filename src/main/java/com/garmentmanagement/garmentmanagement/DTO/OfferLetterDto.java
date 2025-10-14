// DTO: OfferLetterDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferLetterDto {
    private Long id;
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private Long jobPostingId;
    private String jobTitle;
    private String offerCode;
    private LocalDate offerDate;
    private LocalDate joinDate;
    private BigDecimal basicSalary;
    private BigDecimal houseRent;
    private BigDecimal medicalAllowance;
    private BigDecimal otherAllowances;
    private BigDecimal grossSalary;
    private Integer probationPeriod;
    private String status;
    private LocalDate responseDate;
    private String responseNotes;
}