package com.garmentmanagement.garmentmanagement.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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
    private String department;
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
    private BigDecimal salary;

}
