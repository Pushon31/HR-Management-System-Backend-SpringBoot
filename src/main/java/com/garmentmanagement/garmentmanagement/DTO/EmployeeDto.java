package com.garmentmanagement.garmentmanagement.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String email;
    private String nidNumber;
    private String bankAccountNumber;
    private String gender;
    private String maritalStatus;
    private Long departmentId;
    @JsonIgnore
    private String departmentName;
    private LocalDate birthDate;
    private LocalDate joinDate;
    private String phoneNumber;
    private String emergencyContact;
    private String address;
    private String designation;
    private String employeeType;
    private String shift;
    private BigDecimal basicSalary;
    private String photoUrl;
    private Long managerId;
    @JsonIgnore
    private String managerName;
    private String status;
}