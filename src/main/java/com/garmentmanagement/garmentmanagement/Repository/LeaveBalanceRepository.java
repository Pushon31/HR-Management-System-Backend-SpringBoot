package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Entity.LeaveBalance;
import com.garmentmanagement.garmentmanagement.Entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // Basic find methods
//    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);
//    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, Integer year);
//
//    // Employee specific
//    List<LeaveBalance> findByEmployeeId(Long employeeId);
//    List<LeaveBalance> findByEmployeeIdAndYear(Long employeeId, Integer year);

    // Leave type specific
    List<LeaveBalance> findByLeaveTypeId(Long leaveTypeId);

    // Year specific
    List<LeaveBalance> findByYear(Integer year);

    // Check existence
    boolean existsByEmployeeIdAndLeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, Integer year);

    // Dashboard queries
    @Query("SELECT lb.leaveType.name, SUM(lb.usedDays) FROM LeaveBalance lb WHERE lb.year = :year GROUP BY lb.leaveType.name")
    List<Object[]> getTotalUsedDaysByLeaveType(Integer year);

    @Query("SELECT lb.employee.department.name, AVG(lb.usedDays) FROM LeaveBalance lb WHERE lb.year = :year GROUP BY lb.employee.department.name")
    List<Object[]> getAverageLeaveUsageByDepartment(Integer year);

    // Low balance alerts
    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.remainingDays < :threshold AND lb.year = :year")
    List<LeaveBalance> findLowBalanceAlerts(Integer threshold, Integer year);

    // Employee with highest leave usage
    @Query("SELECT lb.employee.firstName, lb.employee.lastName, SUM(lb.usedDays) as totalUsed " +
            "FROM LeaveBalance lb WHERE lb.year = :year " +
            "GROUP BY lb.employee.id, lb.employee.firstName, lb.employee.lastName " +
            "ORDER BY totalUsed DESC")
    List<Object[]> findTopLeaveUsers(Integer year, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(lb) > 0 FROM LeaveBalance lb WHERE lb.employee = :employee AND lb.leaveType = :leaveType AND lb.year = :year")
    boolean existsByEmployeeAndLeaveTypeAndYear(@Param("employee") Employee employee,
                                                @Param("leaveType") LeaveType leaveType,
                                                @Param("year") Integer year);

    // Find balance by employee, leave type, and year
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, Integer year);

    // Find all balances for an employee
    List<LeaveBalance> findByEmployeeId(Long employeeId);

    // Find all balances for an employee in a specific year
    List<LeaveBalance> findByEmployeeIdAndYear(Long employeeId, Integer year);

    int countByYear(int currentYear);
}