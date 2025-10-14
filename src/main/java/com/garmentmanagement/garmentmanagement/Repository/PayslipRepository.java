package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    // ✅ Find by payroll ID
    Optional<Payslip> findByPayrollId(Long payrollId);

    // ✅ Find by payslip code
    Optional<Payslip> findByPayslipCode(String payslipCode);

    // ✅ FIXED: Find by employee ID through payroll relationship
    @Query("SELECT p FROM Payslip p WHERE p.payroll.employee.id = :employeeId")
    List<Payslip> findByEmployeeId(@Param("employeeId") Long employeeId);

    // ✅ FIXED: Find by employee business ID (String) through payroll relationship
    @Query("SELECT p FROM Payslip p WHERE p.payroll.employee.employeeId = :employeeId")
    List<Payslip> findByEmployeeEmployeeId(@Param("employeeId") String employeeId);

    // ✅ Count generated payslips
    @Query("SELECT COUNT(p) FROM Payslip p WHERE p.isGenerated = true")
    Long countGeneratedPayslips();

    // ✅ Find all payslips for a specific pay period
    @Query("SELECT p FROM Payslip p WHERE p.payroll.payPeriod = :payPeriod")
    List<Payslip> findByPayPeriod(@Param("payPeriod") String payPeriod);
}