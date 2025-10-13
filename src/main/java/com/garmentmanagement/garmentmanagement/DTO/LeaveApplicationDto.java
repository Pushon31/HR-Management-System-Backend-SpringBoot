package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private String status;
    private String reason;
    private String remarks;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime appliedDate;
    private LocalDateTime processedDate;
    private String contactNumber;
    private String addressDuringLeave;
    private String departmentName;
}