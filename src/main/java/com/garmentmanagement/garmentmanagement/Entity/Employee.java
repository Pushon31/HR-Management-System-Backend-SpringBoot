package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee extends BaseEntity {

    // Existing Enums
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED
    }

    public enum EmployeeType {
        FULL_TIME, PART_TIME, CONTRACT, INTERN, PROBATION
    }

    public enum EmployeeStatus {
        ACTIVE, INACTIVE, TERMINATED, SUSPENDED, ON_LEAVE
    }

    // ✅ NEW: Employee Work Type Enum
    public enum EmployeeWorkType {
        ONSITE, REMOTE, HYBRID
    }

    // Personal Information
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 30)
    private String employeeId;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String nidNumber;

    @Column(length = 40)
    private String bankAccountNumber;

    // Enums
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EmployeeType employeeType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    // ✅ NEW: Work Type Field
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", length = 20)
    private EmployeeWorkType workType = EmployeeWorkType.ONSITE;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    // Dates
    private LocalDate birthDate;

    @Column(name = "join_date")
    private LocalDate joinDate;

    // Contact Information
    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 20)
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String address;

    // Employment Details
    @Column(length = 50)
    private String designation;

    @Column(length = 30)
    private String shift;

    @Column(precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "profile_pic")
    private String profilePic;

    // ✅ ADD: One-to-One relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ ADD: Constructor with User
    public Employee(User user) {
        this.user = user;
        this.email = user.getEmail();
        this.firstName = extractFirstName(user.getFullName());
        this.lastName = extractLastName(user.getFullName());
        this.employeeId = generateEmployeeId();
        this.status = EmployeeStatus.ACTIVE;
        this.workType = EmployeeWorkType.ONSITE;
        this.employeeType = EmployeeType.FULL_TIME;
        this.joinDate = LocalDate.now();
    }

    private String extractFirstName(String fullName) {
        return fullName.split(" ")[0];
    }

    private String extractLastName(String fullName) {
        String[] names = fullName.split(" ");
        return names.length > 1 ? names[names.length - 1] : "";
    }

    private String generateEmployeeId() {
        return "EMP" + System.currentTimeMillis();
    }
}