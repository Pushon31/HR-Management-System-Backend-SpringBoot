package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.LeaveApplicationDto;
import com.garmentmanagement.garmentmanagement.DTO.LeaveBalanceDto;
import com.garmentmanagement.garmentmanagement.DTO.LeaveTypeDto;
import com.garmentmanagement.garmentmanagement.Entity.LeaveApplication;
import com.garmentmanagement.garmentmanagement.Entity.LeaveType;
import com.garmentmanagement.garmentmanagement.Service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // ==================== LEAVE TYPE ENDPOINTS ====================


    @PostMapping("/types")
    public ResponseEntity<LeaveTypeDto> createLeaveType(@RequestBody LeaveTypeDto leaveTypeDto) {
        LeaveTypeDto createdLeaveType = leaveService.createLeaveType(leaveTypeDto);
        return ResponseEntity.ok(createdLeaveType);
    }


    @GetMapping("/types")
    public ResponseEntity<List<LeaveTypeDto>> getAllLeaveTypes() {
        List<LeaveTypeDto> leaveTypes = leaveService.getAllLeaveTypes();
        return ResponseEntity.ok(leaveTypes);
    }


    @GetMapping("/types/active")
    public ResponseEntity<List<LeaveTypeDto>> getActiveLeaveTypes() {
        List<LeaveTypeDto> leaveTypes = leaveService.getActiveLeaveTypes();
        return ResponseEntity.ok(leaveTypes);
    }


    @GetMapping("/types/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable Long id) {
        LeaveTypeDto leaveType = leaveService.getLeaveTypeById(id);
        return ResponseEntity.ok(leaveType);
    }


    @GetMapping("/types/code/{code}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeByCode(@PathVariable String code) {
        LeaveTypeDto leaveType = leaveService.getLeaveTypeByCode(code);
        return ResponseEntity.ok(leaveType);
    }


    @PutMapping("/types/{id}")
    public ResponseEntity<LeaveTypeDto> updateLeaveType(
            @PathVariable Long id,
            @RequestBody LeaveTypeDto leaveTypeDto) {
        LeaveTypeDto updatedLeaveType = leaveService.updateLeaveType(id, leaveTypeDto);
        return ResponseEntity.ok(updatedLeaveType);
    }


    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        leaveService.deleteLeaveType(id);
        return ResponseEntity.ok().build();
    }

    // ==================== LEAVE APPLICATION ENDPOINTS ====================


    @PostMapping("/applications")
    public ResponseEntity<LeaveApplicationDto> applyForLeave(@RequestBody LeaveApplicationDto leaveApplicationDto) {
        LeaveApplicationDto createdApplication = leaveService.applyForLeave(leaveApplicationDto);
        return ResponseEntity.ok(createdApplication);
    }


    @GetMapping("/applications")
    public ResponseEntity<List<LeaveApplicationDto>> getAllLeaveApplications() {
        List<LeaveApplicationDto> applications = leaveService.getAllLeaveApplications();
        return ResponseEntity.ok(applications);
    }


    @GetMapping("/applications/employee/{employeeId}")
    public ResponseEntity<List<LeaveApplicationDto>> getLeaveApplicationsByEmployee(
            @PathVariable Long employeeId) {
        List<LeaveApplicationDto> applications = leaveService.getLeaveApplicationsByEmployee(employeeId);
        return ResponseEntity.ok(applications);
    }


    @GetMapping("/applications/{id}")
    public ResponseEntity<LeaveApplicationDto> getLeaveApplicationById(@PathVariable Long id) {
        LeaveApplicationDto application = leaveService.getLeaveApplicationById(id);
        return ResponseEntity.ok(application);
    }

    /**
     *  Pending Leave Applications (Admin/Manager)
     *
     */
    @GetMapping("/applications/pending")
    public ResponseEntity<List<LeaveApplicationDto>> getPendingLeaveApplications() {
        List<LeaveApplicationDto> applications = leaveService.getPendingLeaveApplications();
        return ResponseEntity.ok(applications);
    }

    /**
     * Manager-Pending Leaves
     *
     */
    @GetMapping("/applications/manager/{managerId}/pending")
    public ResponseEntity<List<LeaveApplicationDto>> getPendingLeavesForManager(
            @PathVariable Long managerId) {
        List<LeaveApplicationDto> applications = leaveService.getPendingLeavesForManager(managerId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Status Leave Applications
     */
    @GetMapping("/applications/status/{status}")
    public ResponseEntity<List<LeaveApplicationDto>> getLeaveApplicationsByStatus(
            @PathVariable String status) {
        LeaveApplication.LeaveStatus statusEnum = LeaveApplication.LeaveStatus.valueOf(status.toUpperCase());
        List<LeaveApplicationDto> applications = leaveService.getLeaveApplicationsByStatus(statusEnum);
        return ResponseEntity.ok(applications);
    }

    /**
     * Date Range  Leave Applications

     */
    @GetMapping("/applications/date-range")
    public ResponseEntity<List<LeaveApplicationDto>> getLeavesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LeaveApplicationDto> applications = leaveService.getLeavesByDateRange(startDate, endDate);
        return ResponseEntity.ok(applications);
    }

    /**
     *  Department-wise Leave Applications
     *
     */
    @GetMapping("/applications/department/{departmentId}")
    public ResponseEntity<List<LeaveApplicationDto>> getLeavesByDepartment(
            @PathVariable Long departmentId) {
        List<LeaveApplicationDto> applications = leaveService.getLeavesByDepartment(departmentId);
        return ResponseEntity.ok(applications);
    }

    // ==================== LEAVE APPROVAL WORKFLOW ENDPOINTS ====================

    /**
     * Leave Application Approve (Manager/Admin)
     *
     */
    @PutMapping("/applications/{leaveId}/approve")
    public ResponseEntity<LeaveApplicationDto> approveLeave(
            @PathVariable Long leaveId,
            @RequestParam Long approvedBy,
            @RequestParam(required = false) String remarks) {
        LeaveApplicationDto approvedLeave = leaveService.approveLeave(leaveId, approvedBy, remarks);
        return ResponseEntity.ok(approvedLeave);
    }

    /**
     * Leave Application Reject (Manager/Admin)

     */
    @PutMapping("/applications/{leaveId}/reject")
    public ResponseEntity<LeaveApplicationDto> rejectLeave(
            @PathVariable Long leaveId,
            @RequestParam Long approvedBy,
            @RequestParam(required = false) String remarks) {
        LeaveApplicationDto rejectedLeave = leaveService.rejectLeave(leaveId, approvedBy, remarks);
        return ResponseEntity.ok(rejectedLeave);
    }

    /**
     * Leave Application Cancel (Employee)
     *
     */
    @PutMapping("/applications/{leaveId}/cancel")
    public ResponseEntity<LeaveApplicationDto> cancelLeave(
            @PathVariable Long leaveId,
            @RequestParam Long employeeId) {
        LeaveApplicationDto cancelledLeave = leaveService.cancelLeave(leaveId, employeeId);
        return ResponseEntity.ok(cancelledLeave);
    }

    /**
     * Leave Application update while pending
     *
     */
    @PutMapping("/applications/{id}")
    public ResponseEntity<LeaveApplicationDto> updateLeaveApplication(
            @PathVariable Long id,
            @RequestBody LeaveApplicationDto leaveApplicationDto) {
        LeaveApplicationDto updatedApplication = leaveService.updateLeaveApplication(id, leaveApplicationDto);
        return ResponseEntity.ok(updatedApplication);
    }

    // ==================== LEAVE BALANCE ENDPOINTS ====================

    /**
     *  Employee- Leave Balance

     */
    @GetMapping("/balance/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDto>> getEmployeeLeaveBalances(
            @PathVariable Long employeeId) {
        List<LeaveBalanceDto> balances = leaveService.getEmployeeLeaveBalances(employeeId);
        return ResponseEntity.ok(balances);
    }

    /**
     *  Specific Leave Type-Balance
     *
     */
    @GetMapping("/balance/employee/{employeeId}/type/{leaveTypeId}")
    public ResponseEntity<LeaveBalanceDto> getLeaveBalance(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId) {
        LeaveBalanceDto balance = leaveService.getLeaveBalance(employeeId, leaveTypeId);
        return ResponseEntity.ok(balance);
    }

    // ==================== DASHBOARD & REPORTING ENDPOINTS ====================

    /**
     *  Leave Statistics (Dashboard)

     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getLeaveStatistics() {
        Map<String, Long> statistics = leaveService.getLeaveStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Employee-specific Leave Statistics

     */
    @GetMapping("/statistics/employee/{employeeId}")
    public ResponseEntity<Map<String, Long>> getEmployeeLeaveStatistics(
            @PathVariable Long employeeId) {
        Map<String, Long> statistics = leaveService.getEmployeeLeaveStatistics(employeeId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Upcoming Leaves (Calendar)
     *
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<LeaveApplicationDto>> getUpcomingLeaves(
            @RequestParam(defaultValue = "30") int days) {
        List<LeaveApplicationDto> upcomingLeaves = leaveService.getUpcomingLeaves(days);
        return ResponseEntity.ok(upcomingLeaves);
    }

    /**
     * Leave Availability Check

     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkLeaveAvailability(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        boolean isAvailable = leaveService.checkLeaveAvailability(employeeId, leaveTypeId, startDate, endDate);
        return ResponseEntity.ok(isAvailable);
    }

    /**
     * Yearly Leave Balances Initialize (Admin - Year Start)

     */
    @PostMapping("/initialize-yearly-balances")
    public ResponseEntity<Void> initializeYearlyLeaveBalances(@RequestParam Integer year) {
        leaveService.initializeYearlyLeaveBalances(year);
        return ResponseEntity.ok().build();
    }
}