package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeDto {
    private Long id;
    private String name;
    private String code;
    private String category;
    private String description;
    private Integer maxDaysPerYear;
    private Boolean isActive;
    private Boolean requiresApproval;
    private Boolean allowEncashment;
    private Integer carryForwardDays;
}