package com.garmentmanagement.garmentmanagement.Entity;

import lombok.Data;

@Data
public class EmployeeMonthlySummary {
    private String employeeId;
    private String employeeName;
    private String departmentName;
    private int presentDays;
    private int lateDays;
    private int absentDays;
    private int totalWorkingDays;
    private double attendanceRate;
}
