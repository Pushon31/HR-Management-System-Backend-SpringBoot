package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Basic find methods
    Optional<Employee> findByEmployeeId(String employeeId);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByNidNumber(String nidNumber);

    // Exists methods
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByNidNumber(String nidNumber);

    // Department related
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId")
    List<Employee> findByDepartmentId(Long departmentId);

    // Filter methods
    List<Employee> findByEmployeeType(Employee.EmployeeType employeeType);
    List<Employee> findByDesignation(String designation);
    List<Employee> findByStatus(Employee.EmployeeStatus status);

    // Manager related methods
    @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId")
    List<Employee> findSubordinatesByManagerId(Long managerId);

    @Query("SELECT e FROM Employee e WHERE e.manager IS NULL")
    List<Employee> findEmployeesWithoutManager();

    @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId AND e.designation = :designation")
    List<Employee> findSubordinatesByManagerAndDesignation(Long managerId, String designation);
}