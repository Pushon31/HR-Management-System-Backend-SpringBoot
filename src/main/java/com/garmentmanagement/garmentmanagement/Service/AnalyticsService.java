package com.garmentmanagement.garmentmanagement.Service;

import java.util.Map;

public interface AnalyticsService {

    // ==================== DASHBOARD ANALYTICS ====================
    Map<String, Object> getAdminDashboard();
    Map<String, Object> getManagerDashboard(Long managerId);
    Map<String, Object> getEmployeeDashboard(Long employeeId);

    // ==================== EMPLOYEE ANALYTICS ====================
    Map<String, Object> getEmployeeStatistics();
    Map<String, Object> getDepartmentWiseEmployeeCount();
    Map<String, Object> getEmployeeTypeDistribution();

    // ==================== ATTENDANCE ANALYTICS ====================
    Map<String, Object> getAttendanceStatistics();
    Map<String, Object> getMonthlyAttendanceTrend(int year, int month);

    // ==================== TASK ANALYTICS ====================
    Map<String, Object> getTaskStatistics();
    Map<String, Object> getProjectCompletionRates();

    // ==================== LEAVE ANALYTICS ====================
    Map<String, Object> getLeaveStatistics();
    Map<String, Object> getLeaveTrend(int year);

    // ==================== RECRUITMENT ANALYTICS ====================
    Map<String, Object> getRecruitmentStatistics();
}