// DTO: PayslipDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayslipDto {
    private Long id;
    private Long payrollId;
    private String payslipCode;
    private LocalDate issueDate;

    // Employee Info
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String designation;

    // Salary Info
    private Double basicSalary;
    private Double totalAllowances;
    private Double deductions;
    private Double netSalary;
    private String payPeriod;

    private Boolean isGenerated;
    private String status;
    private String departmentName;

}