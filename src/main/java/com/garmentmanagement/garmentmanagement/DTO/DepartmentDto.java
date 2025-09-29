package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String location;
    private BigDecimal budget;
    private String status; // ✅ String format (enum name)
    private LocalDate establishedDate;

    private Long departmentHeadId;
    private String departmentHeadName;

    // Calculated fields
    private Integer employeeCount;
    private Boolean isActive;
}