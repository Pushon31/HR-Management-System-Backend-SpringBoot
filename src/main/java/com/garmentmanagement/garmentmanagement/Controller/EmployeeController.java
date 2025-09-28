package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.EmployeeDto;

import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


    @RestController
    @RequestMapping("/api/employees")
    @RequiredArgsConstructor
    public class EmployeeController {

        private final EmployeeService employeeService;

        @PostMapping
        public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
            EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
            return ResponseEntity.ok(createdEmployee);
        }

        @PutMapping("/{id}")
        public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
            EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
            return ResponseEntity.ok(updatedEmployee);
        }

        @GetMapping("/{id}")
        public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
            EmployeeDto employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        }

        @GetMapping("/employee-id/{employeeId}")
        public ResponseEntity<EmployeeDto> getEmployeeByEmployeeId(@PathVariable String employeeId) {
            EmployeeDto employee = employeeService.getEmployeeByEmployeeId(employeeId);
            return ResponseEntity.ok(employee);
        }

        @GetMapping
        public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
            List<EmployeeDto> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        }

        @GetMapping("/department/{departmentId}")
        public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(@PathVariable Long departmentId) {
            List<EmployeeDto> employees = employeeService.getEmployeesByDepartment(departmentId);
            return ResponseEntity.ok(employees);
        }

        @GetMapping("/type/{employeeType}")
        public ResponseEntity<List<EmployeeDto>> getEmployeesByType(
                @PathVariable String employeeType) {

            // ✅ Convert String to Enum
            Employee.EmployeeType type = Employee.EmployeeType.valueOf(employeeType.toUpperCase());
            List<EmployeeDto> employees = employeeService.getEmployeesByType(type);
            return ResponseEntity.ok(employees);
        }

        @GetMapping("/designation/{designation}")
        public ResponseEntity<List<EmployeeDto>> getEmployeesByDesignation(@PathVariable String designation) {
            List<EmployeeDto> employees = employeeService.getEmployeesByDesignation(designation);
            return ResponseEntity.ok(employees);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok().build();
        }


}
