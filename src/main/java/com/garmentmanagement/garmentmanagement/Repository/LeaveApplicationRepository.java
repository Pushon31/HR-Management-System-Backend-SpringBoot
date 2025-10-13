package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    // Employee specific queries
    List<LeaveApplication> findByEmployeeId(Long employeeId);
    List<LeaveApplication> findByEmployeeIdAndStatus(Long employeeId, LeaveApplication.LeaveStatus status);

    // Date range queries
    List<LeaveApplication> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // Status based queries
    List<LeaveApplication> findByStatus(LeaveApplication.LeaveStatus status);

    // Manager/Approver queries
    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = :managerId AND la.status = 'PENDING'")
    List<LeaveApplication> findPendingLeavesForManager(Long managerId);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.department.id = :departmentId")
    List<LeaveApplication> findByDepartmentId(Long departmentId);

    // Check for overlapping leaves
    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.id = :employeeId AND " +
            "la.status IN ('PENDING', 'APPROVED') AND " +
            "((la.startDate BETWEEN :startDate AND :endDate) OR " +
            "(la.endDate BETWEEN :startDate AND :endDate) OR " +
            "(:startDate BETWEEN la.startDate AND la.endDate))")
    List<LeaveApplication> findOverlappingLeaves(Long employeeId, LocalDate startDate, LocalDate endDate);

    // Count queries for dashboard
    @Query("SELECT COUNT(la) FROM LeaveApplication la WHERE la.status = 'PENDING'")
    Long countPendingApplications();

    @Query("SELECT COUNT(la) FROM LeaveApplication la WHERE la.employee.id = :employeeId AND la.status = 'PENDING'")
    Long countPendingApplicationsByEmployee(Long employeeId);

    // Monthly/yearly reports
    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.id = :employeeId AND " +
            "YEAR(la.startDate) = :year AND MONTH(la.startDate) = :month")
    List<LeaveApplication> findByEmployeeAndMonth(Long employeeId, int year, int month);

    // Find by employee and year
    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.id = :employeeId AND YEAR(la.startDate) = :year")
    List<LeaveApplication> findByEmployeeAndYear(Long employeeId, int year);

    // Dashboard statistics
    @Query("SELECT la.status, COUNT(la) FROM LeaveApplication la GROUP BY la.status")
    List<Object[]> countLeavesByStatus();

    // Upcoming leaves
    @Query("SELECT la FROM LeaveApplication la WHERE la.startDate BETWEEN :startDate AND :endDate AND la.status = 'APPROVED'")
    List<LeaveApplication> findUpcomingLeaves(LocalDate startDate, LocalDate endDate);
}