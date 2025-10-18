package com.garmentmanagement.garmentmanagement.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Boolean active;
    private List<String> roles;

    // Employee info
    private Long employeeId;
    private String employeeCode;
    private String designation;
    private String departmentName;
}