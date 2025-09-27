package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerDto {

    private Long id;

    // Manager আসলে যে Employee তার আইডি বা মিনিমাল ইনফো
    private Long employeeId;          // শুধু আইডি রেফারেন্স রাখা নিরাপদ
    private String employeeName;      // চাইলে দেখানোর সুবিধার জন্য

    private String designation;       // HR Manager, Production Manager etc.
    private LocalDate joiningDate;
    private BigDecimal salary;
    private String status;            // ACTIVE / INACTIVE / ON_LEAVE
    private String address;

    // যে ডিপার্টমেন্টের হেড সেই ডিপার্টমেন্টের রেফারেন্স
    private Long departmentId;
    private String departmentName;    // Optional for display
}
