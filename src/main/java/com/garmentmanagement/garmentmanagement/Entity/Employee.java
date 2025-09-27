package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseAuditEntity;
import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 30)
    private String employeeId;          // নিজের জেনারেট করা কোড (HR/EMP-001 টাইপ)

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String nidNumber;

    @Column(length = 40)
    private String bankAccountNumber;

    @Column(length = 10)
    private String gender;              // Enum করলেও ভালো (MALE, FEMALE, OTHER)

    @Column(length = 20)
    private String maritalStatus;       // Enum করলেও ভালো (SINGLE, MARRIED, etc.)



    @ManyToOne(fetch = FetchType.LAZY)
    private Department departmentId;


    private LocalDate birthDate;

    private LocalDate joinDate;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 20)
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String designation;

    @Column(length = 30)
    private String employeeType;        // Enum করলেও ভালো (FULL_TIME, PART_TIME, etc.)

    @Column(length = 30)
    private String shift;               // Example: DAY, NIGHT

    @Column(precision = 12, scale = 2)
    private BigDecimal basicSalary;

    private String photoUrl;

    @Column(precision = 12, scale = 2)
    private BigDecimal salary;
}
