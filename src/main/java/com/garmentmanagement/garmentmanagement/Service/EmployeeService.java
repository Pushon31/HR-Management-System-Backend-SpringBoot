package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.EmployeeDto;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);
    EmployeeDto updateEmployee(Long id ,EmployeeDto employeeDto);
    EmployeeDto getEmployeeById(Long id);
    List<EmployeeDto> getAllEmployees();
    void deleteEmployee(Long id);
    EmployeeDto getEmployeeByEmployeeId(String employeeId);
    List<EmployeeDto> getEmployeesByDepartment(Long departmentId);
    List<EmployeeDto> getEmployeesByType(Employee.EmployeeType employeeType);
    List<EmployeeDto> getEmployeesByDesignation(String designation);
    List<EmployeeDto> getEmployeesByStatus(Employee.EmployeeStatus status);

    List<EmployeeDto> getManagerTeam(Long managerId);
    void assignManager(Long employeeId, Long managerId);
    EmployeeDto getEmployeeManager(Long employeeId);
    List<EmployeeDto> getEmployeesWithoutManager();
    Map<Employee.EmployeeWorkType, Long> getEmployeeWorkTypeStats();

    EmployeeDto getEmployeeByUserId(Long id);
}
