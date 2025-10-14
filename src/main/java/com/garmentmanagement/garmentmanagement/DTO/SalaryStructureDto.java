package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureDto {
    private Long id;

    // Employee Info
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String designation;
    private String departmentName;

    // Core Salary Components
    private BigDecimal basicSalary;
    private BigDecimal houseRent;
    private BigDecimal medicalAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal otherAllowances;
    private BigDecimal totalDeductions;

    // Calculated Fields
    private BigDecimal grossSalary;
    private BigDecimal netSalary;

    private String status;
}