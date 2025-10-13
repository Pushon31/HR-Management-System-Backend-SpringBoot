package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.LeaveApplicationDto;
import com.garmentmanagement.garmentmanagement.DTO.LeaveBalanceDto;
import com.garmentmanagement.garmentmanagement.DTO.LeaveTypeDto;
import com.garmentmanagement.garmentmanagement.Entity.LeaveApplication;
import com.garmentmanagement.garmentmanagement.Entity.LeaveType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveService {

    // ==================== LEAVE TYPE METHODS ====================
    LeaveTypeDto createLeaveType(LeaveTypeDto leaveTypeDto);
    LeaveTypeDto updateLeaveType(Long id, LeaveTypeDto leaveTypeDto);
    List<LeaveTypeDto> getAllLeaveTypes();
    List<LeaveTypeDto> getActiveLeaveTypes();
    LeaveTypeDto getLeaveTypeById(Long id);
    LeaveTypeDto getLeaveTypeByCode(String code);
    void deleteLeaveType(Long id);

    // ==================== LEAVE APPLICATION METHODS ====================
    LeaveApplicationDto applyForLeave(LeaveApplicationDto leaveApplicationDto);
    LeaveApplicationDto updateLeaveApplication(Long id, LeaveApplicationDto leaveApplicationDto);
    LeaveApplicationDto getLeaveApplicationById(Long id);
    List<LeaveApplicationDto> getAllLeaveApplications();
    List<LeaveApplicationDto> getLeaveApplicationsByEmployee(Long employeeId);
    List<LeaveApplicationDto> getLeaveApplicationsByStatus(LeaveApplication.LeaveStatus status);
    List<LeaveApplicationDto> getPendingLeaveApplications();
    List<LeaveApplicationDto> getPendingLeavesForManager(Long managerId);

    // Approval workflow
    LeaveApplicationDto approveLeave(Long leaveId, Long approvedBy, String remarks);
    LeaveApplicationDto rejectLeave(Long leaveId, Long approvedBy, String remarks);
    LeaveApplicationDto cancelLeave(Long leaveId, Long employeeId);

    // Filter methods
    List<LeaveApplicationDto> getLeavesByDateRange(LocalDate startDate, LocalDate endDate);
    List<LeaveApplicationDto> getLeavesByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);
    List<LeaveApplicationDto> getLeavesByDepartment(Long departmentId);

    // ==================== LEAVE BALANCE METHODS ====================
    LeaveBalanceDto getLeaveBalance(Long employeeId, Long leaveTypeId);
    List<LeaveBalanceDto> getEmployeeLeaveBalances(Long employeeId);
    LeaveBalanceDto updateLeaveBalance(Long employeeId, Long leaveTypeId, Integer additionalDays);
    void initializeYearlyLeaveBalances(Integer year);

    // ==================== DASHBOARD & REPORTING METHODS ====================
    Map<String, Long> getLeaveStatistics();
    Map<String, Long> getEmployeeLeaveStatistics(Long employeeId);
    List<LeaveApplicationDto> getUpcomingLeaves(int days);
    boolean checkLeaveAvailability(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate);
}