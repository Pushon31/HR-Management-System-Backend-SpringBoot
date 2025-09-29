package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Basic find methods
    Optional<Department> findByName(String name);
    Optional<Department> findByCode(String code);

    // Exists methods
    boolean existsByName(String name);
    boolean existsByCode(String code);

    // Filter methods
    List<Department> findByStatus(Department.DepartmentStatus status);

    // Custom query for departments with employees count
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();

    // Find departments by location
    List<Department> findByLocationContainingIgnoreCase(String location);

    // Find department by department head
    @Query("SELECT d FROM Department d WHERE d.departmentHead.id = :employeeId")
    Optional<Department> findByDepartmentHeadId(Long employeeId);
}