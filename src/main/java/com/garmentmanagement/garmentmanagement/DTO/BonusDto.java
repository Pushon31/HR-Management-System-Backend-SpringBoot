// DTO: BonusDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String designation;

    private String bonusType;
    private BigDecimal amount;
    private LocalDate bonusDate;
    private String reason;
    private String status;
    private String departmentName;

}