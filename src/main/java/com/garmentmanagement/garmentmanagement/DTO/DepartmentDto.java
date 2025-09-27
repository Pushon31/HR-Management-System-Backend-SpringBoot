package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
    private Long id;                        // entity-র primary key (যদি দরকার হয়)

    private String name;                     // যেমন: "Knitting", "Finishing", "HR"

    private String code;                     // যেমন: HR-01, ACC-02

    private String description;

    private String location;                 // কোন বিল্ডিং বা ফ্লোর

    private String headOfDepartment;         // ডিপার্টমেন্ট হেডের নাম (বা id)

    private Integer totalEmployees;          // কর্মীর সংখ্যা

    private BigDecimal budget;               // বাজেট

    private String status;                   // ACTIVE / INACTIVE

    private LocalDate establishedDate;       // প্রতিষ্ঠার তারিখ

    // যদি শুধু employee id বা নাম পাঠাতে চান, পুরো entity নয়:
    private Set<Long> employeeIds;
    // অথবা শুধু নাম চাইলে Set<String> employeeNames ও ব্যবহার করা যায়
}
