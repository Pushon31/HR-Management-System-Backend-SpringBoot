package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.DepartmentDto;
import com.garmentmanagement.garmentmanagement.Entity.Department;

import java.util.List;

public interface DepartmentService {

    // Basic CRUD operations
    DepartmentDto createDepartment(DepartmentDto departmentDto);
    DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto);
    DepartmentDto getDepartmentById(Long id);
    DepartmentDto getDepartmentByName(String name);
    DepartmentDto getDepartmentByCode(String code);
    List<DepartmentDto> getAllDepartments();
    void deleteDepartment(Long id);

    // Filter operations
    List<DepartmentDto> getDepartmentsByStatus(Department.DepartmentStatus status);
    List<DepartmentDto> getDepartmentsByLocation(String location);

    // Special operations
    DepartmentDto assignDepartmentHead(Long departmentId, Long employeeId);
    void removeDepartmentHead(Long departmentId);
    Integer getEmployeeCount(Long departmentId);
}