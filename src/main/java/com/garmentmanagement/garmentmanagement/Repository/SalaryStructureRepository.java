package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    Optional<SalaryStructure> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);

    @Query("SELECT ss FROM SalaryStructure ss WHERE ss.status = 'ACTIVE'")
    List<SalaryStructure> findActiveStructures();

    @Query("SELECT ss FROM SalaryStructure ss WHERE ss.employee.department.id = :departmentId")
    List<SalaryStructure> findByDepartmentId(Long departmentId);

    @Query("SELECT AVG(ss.netSalary) FROM SalaryStructure ss WHERE ss.status = 'ACTIVE'")
    Double findAverageSalary();
}