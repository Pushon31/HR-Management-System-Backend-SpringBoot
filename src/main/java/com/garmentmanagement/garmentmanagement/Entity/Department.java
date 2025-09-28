package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "department")
public class Department extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String name;                 // যেমন: "Knitting", "Finishing", "HR"

    @Column(nullable = false, unique = true, length = 20)
    private String code;                 // যেমন: HR-01, ACC-02

    @OneToOne
    @JoinColumn(name = "head_manager_id")
    private Manager headOfDepartment;



    @Column(length = 255)
    private String description;

    @Column(length = 100)
    private String location;             // কোন বিল্ডিং বা ফ্লোর


    private Integer totalEmployees;      // এই ডিপার্টমেন্টে কর্মীর সংখ্যা

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;           // ডিপার্টমেন্ট বাজেট


    @Column(length = 20)
    private String status;               // ACTIVE, INACTIVE

    private LocalDate establishedDate;   // প্রতিষ্ঠার তারিখ


    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<Employee> employees = new HashSet<>();
}
