// DTO: CandidateDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nidNumber;
    private String gender;
    private LocalDate birthDate;
    private String currentAddress;
    private String permanentAddress;
    private String currentJobTitle;
    private String currentCompany;
    private String experience;
    private String education;
    private String skills;
    private String resumeUrl;
    private String coverLetter;
    private BigDecimal expectedSalary;
    private String status;
    private String notes;

    // Additional info
    private Integer totalApplications;
    private LocalDate createdDate;
}