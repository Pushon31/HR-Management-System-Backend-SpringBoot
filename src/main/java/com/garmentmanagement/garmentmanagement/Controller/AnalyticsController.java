package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.Service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // ==================== DASHBOARD ANALYTICS ====================

    /**
     * ✅ এডমিন ড্যাশবোর্ড এনালিটিক্স
     */
    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> dashboard = analyticsService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * ✅ ম্যানেজার ড্যাশবোর্ড এনালিটিক্স
     */
    @GetMapping("/manager/dashboard/{managerId}")
    public ResponseEntity<Map<String, Object>> getManagerDashboard(@PathVariable Long managerId) {
        Map<String, Object> dashboard = analyticsService.getManagerDashboard(managerId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * ✅ এমপ্লয়ী ড্যাশবোর্ড এনালিটিক্স
     */
    @GetMapping("/employee/dashboard/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard(@PathVariable Long employeeId) {
        Map<String, Object> dashboard = analyticsService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(dashboard);
    }

    // ==================== EMPLOYEE ANALYTICS ====================

    /**
     * ✅ এমপ্লয়ী স্ট্যাটিস্টিক্স
     */
    @GetMapping("/employees/statistics")
    public ResponseEntity<Map<String, Object>> getEmployeeStatistics() {
        Map<String, Object> stats = analyticsService.getEmployeeStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ ডিপার্টমেন্ট-ওয়াইজ এমপ্লয়ী কাউন্ট
     */
    @GetMapping("/employees/department-wise")
    public ResponseEntity<Map<String, Object>> getDepartmentWiseEmployeeCount() {
        Map<String, Object> stats = analyticsService.getDepartmentWiseEmployeeCount();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ এমপ্লয়ী টাইপ ডিস্ট্রিবিউশন
     */
    @GetMapping("/employees/type-distribution")
    public ResponseEntity<Map<String, Object>> getEmployeeTypeDistribution() {
        Map<String, Object> distribution = analyticsService.getEmployeeTypeDistribution();
        return ResponseEntity.ok(distribution);
    }

    // ==================== ATTENDANCE ANALYTICS ====================

    /**
     * ✅ অ্যাটেনডেন্স স্ট্যাটিস্টিক্স
     */
    @GetMapping("/attendance/statistics")
    public ResponseEntity<Map<String, Object>> getAttendanceStatistics() {
        Map<String, Object> stats = analyticsService.getAttendanceStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ মাসিক অ্যাটেনডেন্স ট্রেন্ড
     */
    @GetMapping("/attendance/monthly-trend")
    public ResponseEntity<Map<String, Object>> getMonthlyAttendanceTrend(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> trend = analyticsService.getMonthlyAttendanceTrend(year, month);
        return ResponseEntity.ok(trend);
    }

    // ==================== TASK ANALYTICS ====================

    /**
     * ✅ টাস্ক স্ট্যাটিস্টিক্স
     */
    @GetMapping("/tasks/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics() {
        Map<String, Object> stats = analyticsService.getTaskStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ প্রজেক্ট কমপ্লিশন রেট
     */
    @GetMapping("/projects/completion-rates")
    public ResponseEntity<Map<String, Object>> getProjectCompletionRates() {
        Map<String, Object> rates = analyticsService.getProjectCompletionRates();
        return ResponseEntity.ok(rates);
    }

    // ==================== LEAVE ANALYTICS ====================

    /**
     * ✅ লিভ স্ট্যাটিস্টিক্স
     */
    @GetMapping("/leaves/statistics")
    public ResponseEntity<Map<String, Object>> getLeaveStatistics() {
        Map<String, Object> stats = analyticsService.getLeaveStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ লিভ ট্রেন্ড (বছর অনুযায়ী)
     */
    @GetMapping("/leaves/trend")
    public ResponseEntity<Map<String, Object>> getLeaveTrend(@RequestParam int year) {
        Map<String, Object> trend = analyticsService.getLeaveTrend(year);
        return ResponseEntity.ok(trend);
    }

    // ==================== RECRUITMENT ANALYTICS ====================

    /**
     * ✅ রিক্রুটমেন্ট স্ট্যাটিস্টিক্স
     */
    @GetMapping("/recruitment/statistics")
    public ResponseEntity<Map<String, Object>> getRecruitmentStatistics() {
        Map<String, Object> stats = analyticsService.getRecruitmentStatistics();
        return ResponseEntity.ok(stats);
    }
}