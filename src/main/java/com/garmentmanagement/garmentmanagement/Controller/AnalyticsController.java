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


    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> dashboard = analyticsService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }


    @GetMapping("/manager/dashboard/{managerId}")
    public ResponseEntity<Map<String, Object>> getManagerDashboard(@PathVariable Long managerId) {
        Map<String, Object> dashboard = analyticsService.getManagerDashboard(managerId);
        return ResponseEntity.ok(dashboard);
    }


    @GetMapping("/employee/dashboard/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard(@PathVariable Long employeeId) {
        Map<String, Object> dashboard = analyticsService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(dashboard);
    }

    // ==================== EMPLOYEE ANALYTICS ====================


    @GetMapping("/employees/statistics")
    public ResponseEntity<Map<String, Object>> getEmployeeStatistics() {
        Map<String, Object> stats = analyticsService.getEmployeeStatistics();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/employees/department-wise")
    public ResponseEntity<Map<String, Object>> getDepartmentWiseEmployeeCount() {
        Map<String, Object> stats = analyticsService.getDepartmentWiseEmployeeCount();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/employees/type-distribution")
    public ResponseEntity<Map<String, Object>> getEmployeeTypeDistribution() {
        Map<String, Object> distribution = analyticsService.getEmployeeTypeDistribution();
        return ResponseEntity.ok(distribution);
    }

    // ==================== ATTENDANCE ANALYTICS ====================


    @GetMapping("/attendance/statistics")
    public ResponseEntity<Map<String, Object>> getAttendanceStatistics() {
        Map<String, Object> stats = analyticsService.getAttendanceStatistics();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/attendance/monthly-trend")
    public ResponseEntity<Map<String, Object>> getMonthlyAttendanceTrend(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> trend = analyticsService.getMonthlyAttendanceTrend(year, month);
        return ResponseEntity.ok(trend);
    }

    // ==================== TASK ANALYTICS ====================


    @GetMapping("/tasks/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics() {
        Map<String, Object> stats = analyticsService.getTaskStatistics();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/projects/completion-rates")
    public ResponseEntity<Map<String, Object>> getProjectCompletionRates() {
        Map<String, Object> rates = analyticsService.getProjectCompletionRates();
        return ResponseEntity.ok(rates);
    }

    // ==================== LEAVE ANALYTICS ====================


    @GetMapping("/leaves/statistics")
    public ResponseEntity<Map<String, Object>> getLeaveStatistics() {
        Map<String, Object> stats = analyticsService.getLeaveStatistics();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/leaves/trend")
    public ResponseEntity<Map<String, Object>> getLeaveTrend(@RequestParam int year) {
        Map<String, Object> trend = analyticsService.getLeaveTrend(year);
        return ResponseEntity.ok(trend);
    }

    // ==================== RECRUITMENT ANALYTICS ====================


    @GetMapping("/recruitment/statistics")
    public ResponseEntity<Map<String, Object>> getRecruitmentStatistics() {
        Map<String, Object> stats = analyticsService.getRecruitmentStatistics();
        return ResponseEntity.ok(stats);
    }
}