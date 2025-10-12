// MonthlySummaryDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import com.garmentmanagement.garmentmanagement.Entity.EmployeeMonthlySummary;
import lombok.Data;
import java.util.List;

@Data
public class MonthlySummaryDto {
    private int year;
    private int month;
    private int totalEmployees;
    private int totalPresent;
    private int totalLate;
    private int totalAbsent;
    private double attendanceRate;
    private List<EmployeeMonthlySummary> employeeSummaries;
}


