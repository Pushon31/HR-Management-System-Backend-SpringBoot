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
@Table(name = "departments")
public class Department extends BaseEntity {

    // ✅ Enums inside Entity
    public enum DepartmentStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_head_id")
    private Employee departmentHead;

    @Column(length = 255)
    private String description;

    @Column(length = 100)
    private String location;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DepartmentStatus status = DepartmentStatus.ACTIVE;

    private LocalDate establishedDate;

    // ❌ REMOVE THIS - It's causing ConcurrentModificationException
    // @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<Employee> employees = new HashSet<>();

    // ✅ Add helper method instead
    public void addEmployee(Employee employee) {
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employee.setDepartment(null);
    }
}