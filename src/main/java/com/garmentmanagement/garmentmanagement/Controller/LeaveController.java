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

    /**
     * ✅ নতুন Leave Type তৈরি করা
     * বাংলায়: নতুন ছুটির ধরন তৈরি
     */
    @PostMapping("/types")
    public ResponseEntity<LeaveTypeDto> createLeaveType(@RequestBody LeaveTypeDto leaveTypeDto) {
        LeaveTypeDto createdLeaveType = leaveService.createLeaveType(leaveTypeDto);
        return ResponseEntity.ok(createdLeaveType);
    }

    /**
     * ✅ সব Leave Types পাওয়া
     * বাংলায়: সব ছুটির ধরনগুলো পাওয়া
     */
    @GetMapping("/types")
    public ResponseEntity<List<LeaveTypeDto>> getAllLeaveTypes() {
        List<LeaveTypeDto> leaveTypes = leaveService.getAllLeaveTypes();
        return ResponseEntity.ok(leaveTypes);
    }

    /**
     * ✅ শুধু Active Leave Types পাওয়া
     * বাংলায়: শুধু সক্রিয় ছুটির ধরনগুলো পাওয়া
     */
    @GetMapping("/types/active")
    public ResponseEntity<List<LeaveTypeDto>> getActiveLeaveTypes() {
        List<LeaveTypeDto> leaveTypes = leaveService.getActiveLeaveTypes();
        return ResponseEntity.ok(leaveTypes);
    }

    /**
     * ✅ ID দিয়ে Leave Type পাওয়া
     * বাংলায়: আইডি দিয়ে ছুটির ধরন পাওয়া
     */
    @GetMapping("/types/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable Long id) {
        LeaveTypeDto leaveType = leaveService.getLeaveTypeById(id);
        return ResponseEntity.ok(leaveType);
    }

    /**
     * ✅ Code দিয়ে Leave Type পাওয়া
     * বাংলায়: কোড দিয়ে ছুটির ধরন পাওয়া (যেমন: "AL", "SL")
     */
    @GetMapping("/types/code/{code}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeByCode(@PathVariable String code) {
        LeaveTypeDto leaveType = leaveService.getLeaveTypeByCode(code);
        return ResponseEntity.ok(leaveType);
    }

    /**
     * ✅ Leave Type আপডেট করা
     * বাংলায়: ছুটির ধরন আপডেট করা
     */
    @PutMapping("/types/{id}")
    public ResponseEntity<LeaveTypeDto> updateLeaveType(
            @PathVariable Long id,
            @RequestBody LeaveTypeDto leaveTypeDto) {
        LeaveTypeDto updatedLeaveType = leaveService.updateLeaveType(id, leaveTypeDto);
        return ResponseEntity.ok(updatedLeaveType);
    }

    /**
     * ✅ Leave Type ডিলিট করা
     * বাংলায়: ছুটির ধরন ডিলিট করা
     */
    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        leaveService.deleteLeaveType(id);
        return ResponseEntity.ok().build();
    }

    // ==================== LEAVE APPLICATION ENDPOINTS ====================

    /**
     * ✅ নতুন Leave Application তৈরি করা (ছুটির আবেদন)
     * বাংলায়: নতুন ছুটির আবেদন জমা দেওয়া
     */
    @PostMapping("/applications")
    public ResponseEntity<LeaveApplicationDto> applyForLeave(@RequestBody LeaveApplicationDto leaveApplicationDto) {
        LeaveApplicationDto createdApplication = leaveService.applyForLeave(leaveApplicationDto);
        return ResponseEntity.ok(createdApplication);
    }

    /**
     * ✅ সব Leave Applications পাওয়া (Admin)
     * বাংলায়: সব ছুটির আবেদনগুলো দেখা (এডমিনের জন্য)
     */
    @GetMapping("/applications")
    public ResponseEntity<List<LeaveApplicationDto>> getAllLeaveApplications() {
        List<LeaveApplicationDto> applications = leaveService.getAllLeaveApplications();
        return ResponseEntity.ok(applications);
    }

    /**
     * ✅ নির্দিষ্ট Employee-এর সব Leave Applications পাওয়া
     * বাংলায়: একটি কর্মচারীর সব ছুটির আবেদন দেখা
     */
    @GetMapping("/applications/employee/{employeeId}")
    public ResponseEntity<List<LeaveApplicationDto>> getLeaveApplicationsByEmployee(
            @PathVariable Long employeeId) {
        List<LeaveApplicationDto> applications = leaveService.getLeaveApplicationsByEmployee(employeeId);
        return ResponseEntity.ok(applications);
    }

    /**
     * ✅ Leave Application ID দিয়ে Details পাওয়া
     * বাংলায়: আবেদন আইডি দিয়ে ছুটির বিস্তারিত তথ্য পাওয়া
     */
    @GetMapping("/applications/{id}")
    public ResponseEntity<LeaveApplicationDto> getLeaveApplicationById(@PathVariable Long id) {
        LeaveApplicationDto application = leaveService.getLeaveApplicationById(id);
        return ResponseEntity.ok(application);
    }

    /**
     * ✅ Pending Leave Applications পাওয়া (Admin/Manager)
     * বাংলায়: মঞ্জুরির জন্য অপেক্ষমান ছুটির আবেদনগুলো দেখা
     */
    @GetMapping("/applications/pending")
    public ResponseEntity<List<LeaveApplicationDto>> getPendingLeaveApplications() {
        List<LeaveApplicationDto> applications = leaveService.getPendingLeaveApplications();
        return ResponseEntity.ok(applications);
    }

    /**
     * ✅ Manager-এর জন্য Pending Leaves পাওয়া
     * বাংলায়: একজন ম্যানেজারের টিমের অপেক্ষমান ছুটির আবেদনগুলো দেখা
     */
    @GetMapping("/applications/manager/{managerId}/pending")
    public ResponseEntity<List<LeaveApplicationDto>> getPendingLeavesForManager(
            @PathVariable Long managerId) {
        List<LeaveApplicationDto> applications = leaveService.getPendingLeavesForManager(managerId);
        return ResponseEntity.ok(applications);
    }

    /**
     * ✅ Status অনুযায়ী Leave Applications পাওয়া
     * বাংলায়: অবস্থা অনুসারে ছুটির আবেদনগুলো ফিল্টার করা
     */
    @GetMapping("/applications/status/{status}")
    public ResponseEntity<List<LeaveApplicationDto>> getLeaveApplicationsByStatus(
            @PathVariable String status) {
        LeaveApplication.LeaveStatus statusEnum = LeaveApplication.LeaveStatus.valueOf(status.toUpperCase());
        List<LeaveApplicationDto> applications = leaveService.getLeaveApplicationsByStatus(statusEnum);
        return ResponseEntity.ok(applications);
    }

    /**
     * ✅ Date Range অনুযায়ী Leave Applications পাওয়া
     * বাংলায়: তারিখের range অনুসারে ছুটির আবেদনগুলো দেখা
     */
    @GetMapping("/applications/date-range")
    public ResponseEntity<List<LeaveApplicationDto>> getLeavesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LeaveApplicationDto> applications = leaveService.getLeavesByDateRange(startDate, endDate);
        return ResponseEntity.ok(applications);
    }

    /**
     * ✅ Department-wise Leave Applications পাওয়া
     * বাংলায়: ডিপার্টমেন্ট অনুসারে ছুটির আবেদনগুলো দেখা
     */
    @GetMapping("/applications/department/{departmentId}")
    public ResponseEntity<List<LeaveApplicationDto>> getLeavesByDepartment(
            @PathVariable Long departmentId) {
        List<LeaveApplicationDto> applications = leaveService.getLeavesByDepartment(departmentId);
        return ResponseEntity.ok(applications);
    }

    // ==================== LEAVE APPROVAL WORKFLOW ENDPOINTS ====================

    /**
     * ✅ Leave Application Approve করা (Manager/Admin)
     * বাংলায়: ছুটির আবেদন মঞ্জুর করা
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
     * ✅ Leave Application Reject করা (Manager/Admin)
     * বাংলায়: ছুটির আবেদন প্রত্যাখ্যান করা
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
     * ✅ Leave Application Cancel করা (Employee)
     * বাংলায়: ছুটির আবেদন বাতিল করা (কর্মচারী নিজে)
     */
    @PutMapping("/applications/{leaveId}/cancel")
    public ResponseEntity<LeaveApplicationDto> cancelLeave(
            @PathVariable Long leaveId,
            @RequestParam Long employeeId) {
        LeaveApplicationDto cancelledLeave = leaveService.cancelLeave(leaveId, employeeId);
        return ResponseEntity.ok(cancelledLeave);
    }

    /**
     * ✅ Leave Application আপডেট করা (Employee - শুধু PENDING অবস্থায়)
     * বাংলায়: ছুটির আবেদন আপডেট করা (শুধু অপেক্ষমান অবস্থায়)
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
     * ✅ Employee-এর Leave Balance পাওয়া
     * বাংলায়: একজন কর্মচারীর ছুটির ব্যালেন্স দেখা
     */
    @GetMapping("/balance/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDto>> getEmployeeLeaveBalances(
            @PathVariable Long employeeId) {
        List<LeaveBalanceDto> balances = leaveService.getEmployeeLeaveBalances(employeeId);
        return ResponseEntity.ok(balances);
    }

    /**
     * ✅ Specific Leave Type-এর Balance পাওয়া
     * বাংলায়: নির্দিষ্ট ধরনের ছুটির ব্যালেন্স দেখা
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
     * ✅ Leave Statistics পাওয়া (Dashboard-এর জন্য)
     * বাংলায়: ছুটি সম্পর্কিত পরিসংখ্যান (ড্যাশবোর্ডের জন্য)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getLeaveStatistics() {
        Map<String, Long> statistics = leaveService.getLeaveStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * ✅ Employee-specific Leave Statistics
     * বাংলায়: নির্দিষ্ট কর্মচারীর ছুটি পরিসংখ্যান
     */
    @GetMapping("/statistics/employee/{employeeId}")
    public ResponseEntity<Map<String, Long>> getEmployeeLeaveStatistics(
            @PathVariable Long employeeId) {
        Map<String, Long> statistics = leaveService.getEmployeeLeaveStatistics(employeeId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * ✅ Upcoming Leaves পাওয়া (Calendar-এর জন্য)
     * বাংলায়: আসন্ন ছুটিগুলো দেখা (ক্যালেন্ডারের জন্য)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<LeaveApplicationDto>> getUpcomingLeaves(
            @RequestParam(defaultValue = "30") int days) {
        List<LeaveApplicationDto> upcomingLeaves = leaveService.getUpcomingLeaves(days);
        return ResponseEntity.ok(upcomingLeaves);
    }

    /**
     * ✅ Leave Availability Check করা
     * বাংলায়: ছুটি নেওয়া যাবে কিনা চেক করা
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
     * ✅ Yearly Leave Balances Initialize করা (Admin - Year Start-এ)
     * বাংলায়: বছরের শুরুতে সব কর্মচারীর ছুটির ব্যালেন্স ইনিশিয়ালাইজ করা
     */
    @PostMapping("/initialize-yearly-balances")
    public ResponseEntity<Void> initializeYearlyLeaveBalances(@RequestParam Integer year) {
        leaveService.initializeYearlyLeaveBalances(year);
        return ResponseEntity.ok().build();
    }
}