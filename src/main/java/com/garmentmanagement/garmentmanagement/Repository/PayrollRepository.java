package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll> findByEmployeeIdAndPayPeriod(Long employeeId, YearMonth payPeriod);
    List<Payroll> findByEmployeeId(Long employeeId);
    List<Payroll> findByPayPeriod(YearMonth payPeriod);

    @Query("SELECT p FROM Payroll p WHERE p.employee.department.id = :departmentId AND p.payPeriod = :payPeriod")
    List<Payroll> findByDepartmentAndPeriod(Long departmentId, YearMonth payPeriod);

    @Query("SELECT p FROM Payroll p WHERE p.status = 'PENDING'")
    List<Payroll> findPendingPayrolls();

    boolean existsByEmployeeIdAndPayPeriod(Long employeeId, YearMonth payPeriod);

    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.payPeriod = :payPeriod")
    Long countByPayPeriod(YearMonth payPeriod);

    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.payPeriod = :payPeriod")
    Double getTotalSalaryExpenseByPeriod(YearMonth payPeriod);
}