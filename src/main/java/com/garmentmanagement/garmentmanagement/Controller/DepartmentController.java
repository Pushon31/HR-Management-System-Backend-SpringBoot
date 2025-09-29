package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.DepartmentDto;
import com.garmentmanagement.garmentmanagement.Entity.Department;
import com.garmentmanagement.garmentmanagement.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return ResponseEntity.ok(createdDepartment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updatedDepartment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DepartmentDto> getDepartmentByName(@PathVariable String name) {
        DepartmentDto department = departmentService.getDepartmentByName(name);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DepartmentDto> getDepartmentByCode(@PathVariable String code) {
        DepartmentDto department = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(department);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsByStatus(@PathVariable String status) {
        Department.DepartmentStatus statusEnum = Department.DepartmentStatus.valueOf(status.toUpperCase());
        List<DepartmentDto> departments = departmentService.getDepartmentsByStatus(statusEnum);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsByLocation(@PathVariable String location) {
        List<DepartmentDto> departments = departmentService.getDepartmentsByLocation(location);
        return ResponseEntity.ok(departments);
    }

    @PostMapping("/{departmentId}/assign-head/{employeeId}")
    public ResponseEntity<DepartmentDto> assignDepartmentHead(
            @PathVariable Long departmentId,
            @PathVariable Long employeeId) {
        DepartmentDto department = departmentService.assignDepartmentHead(departmentId, employeeId);
        return ResponseEntity.ok(department);
    }

    @DeleteMapping("/{departmentId}/remove-head")
    public ResponseEntity<Void> removeDepartmentHead(@PathVariable Long departmentId) {
        departmentService.removeDepartmentHead(departmentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{departmentId}/employee-count")
    public ResponseEntity<Integer> getEmployeeCount(@PathVariable Long departmentId) {
        Integer count = departmentService.getEmployeeCount(departmentId);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }
}