package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.Entity.*;
import com.garmentmanagement.garmentmanagement.Repository.*;
import com.garmentmanagement.garmentmanagement.Service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImplementation implements AnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;

    // ==================== DASHBOARD ANALYTICS ====================

    @Override
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // Employee Statistics
        dashboard.put("totalEmployees", employeeRepository.count());
        dashboard.put("activeEmployees", employeeRepository.findByStatus(Employee.EmployeeStatus.ACTIVE).size());

        // Department Statistics
        dashboard.put("totalDepartments", departmentRepository.count());

        // Today's Attendance
        long presentToday = attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(), Attendance.AttendanceStatus.PRESENT).size();
        long lateToday = attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(), Attendance.AttendanceStatus.LATE).size();
        dashboard.put("presentToday", presentToday);
        dashboard.put("lateToday", lateToday);
        dashboard.put("absentToday", getTotalEmployees() - presentToday);

        // Task Statistics
        dashboard.put("totalTasks", taskRepository.count());
        dashboard.put("completedTasks", taskRepository.findByStatus(Task.TaskStatus.COMPLETED).size());
        dashboard.put("overdueTasks", taskRepository.findOverdueTasks(LocalDate.now()).size());

        // Project Statistics
        dashboard.put("totalProjects", projectRepository.count());
        dashboard.put("activeProjects", projectRepository.findByStatus(Project.ProjectStatus.IN_PROGRESS).size());

        // Leave Statistics
        dashboard.put("pendingLeaves", leaveApplicationRepository.countPendingApplications());

        // Recruitment Statistics
        dashboard.put("openPositions", jobPostingRepository.countOpenPositions());
        dashboard.put("totalCandidates", candidateRepository.count());

        return dashboard;
    }

    @Override
    public Map<String, Object> getManagerDashboard(Long managerId) {
        Map<String, Object> dashboard = new HashMap<>();

        // Get manager's department
        Long departmentId = getManagerDepartmentId(managerId);

        // Team Statistics
        List<Employee> teamMembers = employeeRepository.findByDepartmentId(departmentId);
        dashboard.put("teamSize", teamMembers.size());

        // Team Task Statistics
        List<Task> teamTasks = taskRepository.findByDepartmentId(departmentId);
        dashboard.put("teamTotalTasks", teamTasks.size());
        dashboard.put("teamCompletedTasks", teamTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count());
        dashboard.put("teamOverdueTasks", teamTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.OVERDUE)
                .count());

        // Team Attendance (Today)
        long teamPresent = teamMembers.stream()
                .filter(employee -> {
                    // Check if employee is present today
                    return attendanceRepository.findByEmployeeIdAndAttendanceDate(
                                    employee.getId(), LocalDate.now())
                            .map(att -> att.getStatus() == Attendance.AttendanceStatus.PRESENT ||
                                    att.getStatus() == Attendance.AttendanceStatus.LATE)
                            .orElse(false);
                })
                .count();
        dashboard.put("teamPresentToday", teamPresent);
        dashboard.put("teamAbsentToday", teamMembers.size() - teamPresent);

        return dashboard;
    }

    @Override
    public Map<String, Object> getEmployeeDashboard(Long employeeId) {
        Map<String, Object> dashboard = new HashMap<>();

        // Employee Tasks
        List<Task> employeeTasks = taskRepository.findByAssignedToId(employeeId);
        dashboard.put("totalTasks", employeeTasks.size());
        dashboard.put("completedTasks", employeeTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count());
        dashboard.put("pendingTasks", employeeTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.PENDING)
                .count());

        // Average Completion
        double avgCompletion = employeeTasks.stream()
                .mapToInt(task -> task.getCompletionPercentage() != null ? task.getCompletionPercentage() : 0)
                .average()
                .orElse(0.0);
        dashboard.put("averageCompletion", Math.round(avgCompletion * 100.0) / 100.0);

        // Attendance (This Month)
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Attendance> monthAttendance = attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(
                employeeId, startOfMonth, endOfMonth);

        long presentDays = monthAttendance.stream()
                .filter(att -> att.getStatus() == Attendance.AttendanceStatus.PRESENT ||
                        att.getStatus() == Attendance.AttendanceStatus.LATE)
                .count();

        dashboard.put("monthlyPresentDays", presentDays);
        dashboard.put("monthlyWorkingDays", LocalDate.now().getDayOfMonth()); // Approximate

        // Leave Balance
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByEmployeeIdAndYear(
                employeeId, LocalDate.now().getYear());
        int totalLeaveBalance = leaveBalances.stream()
                .mapToInt(LeaveBalance::getRemainingDays)
                .sum();
        dashboard.put("totalLeaveBalance", totalLeaveBalance);

        return dashboard;
    }

    // ==================== EMPLOYEE ANALYTICS ====================

    @Override
    public Map<String, Object> getEmployeeStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.findByStatus(Employee.EmployeeStatus.ACTIVE).size();

        stats.put("totalEmployees", totalEmployees);
        stats.put("activeEmployees", activeEmployees);
        stats.put("inactiveEmployees", totalEmployees - activeEmployees);

        // Average calculation
        if (totalEmployees > 0) {
            double activePercentage = (activeEmployees * 100.0) / totalEmployees;
            stats.put("activePercentage", Math.round(activePercentage * 100.0) / 100.0);
        } else {
            stats.put("activePercentage", 0.0);
        }

        return stats;
    }

    @Override
    public Map<String, Object> getDepartmentWiseEmployeeCount() {
        Map<String, Object> departmentStats = new HashMap<>();

        List<Department> departments = departmentRepository.findAll();
        for (Department department : departments) {
            Integer employeeCount = employeeRepository.countByDepartmentId(department.getId());
            int count = (employeeCount != null) ? employeeCount : 0;

            departmentStats.put(department.getName(), count);
        }

        return departmentStats;
    }

    @Override
    public Map<String, Object> getEmployeeTypeDistribution() {
        Map<String, Object> typeDistribution = new HashMap<>();

        for (Employee.EmployeeType type : Employee.EmployeeType.values()) {
            long count = employeeRepository.findByEmployeeType(type).size();
            typeDistribution.put(type.name(), count);
        }

        return typeDistribution;
    }

    // ==================== ATTENDANCE ANALYTICS ====================

    @Override
    public Map<String, Object> getAttendanceStatistics() {
        Map<String, Object> stats = new HashMap<>();

        LocalDate today = LocalDate.now();

        // Today's attendance
        long present = attendanceRepository.findByAttendanceDateAndStatus(
                today, Attendance.AttendanceStatus.PRESENT).size();
        long late = attendanceRepository.findByAttendanceDateAndStatus(
                today, Attendance.AttendanceStatus.LATE).size();
        long absent = attendanceRepository.findByAttendanceDateAndStatus(
                today, Attendance.AttendanceStatus.ABSENT).size();

        stats.put("presentToday", present);
        stats.put("lateToday", late);
        stats.put("absentToday", absent);
        stats.put("totalMarked", present + late + absent);

        // Monthly average
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<Attendance> monthAttendances = attendanceRepository.findByAttendanceDateBetween(
                startOfMonth, endOfMonth);

        long totalPresent = monthAttendances.stream()
                .filter(att -> att.getStatus() == Attendance.AttendanceStatus.PRESENT ||
                        att.getStatus() == Attendance.AttendanceStatus.LATE)
                .count();

        int workingDays = today.getDayOfMonth(); // Approximate
        double averageAttendance = workingDays > 0 ? (totalPresent * 100.0) / (workingDays * getTotalEmployees()) : 0;

        stats.put("monthlyAverageAttendance", Math.round(averageAttendance * 100.0) / 100.0);

        return stats;
    }

    @Override
    public Map<String, Object> getMonthlyAttendanceTrend(int year, int month) {
        Map<String, Object> trend = new HashMap<>();

        // Simple implementation - you can enhance this for daily trends
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth());

        List<Attendance> monthlyAttendance = attendanceRepository.findByAttendanceDateBetween(startDate, endDate);

        long totalPresent = monthlyAttendance.stream()
                .filter(att -> att.getStatus() == Attendance.AttendanceStatus.PRESENT ||
                        att.getStatus() == Attendance.AttendanceStatus.LATE)
                .count();

        trend.put("year", year);
        trend.put("month", month);
        trend.put("totalPresent", totalPresent);
        trend.put("totalWorkingDays", startDate.lengthOfMonth());
        trend.put("attendanceRate", Math.round((totalPresent * 100.0) / (startDate.lengthOfMonth() * getTotalEmployees())));

        return trend;
    }

    // ==================== TASK ANALYTICS ====================

    @Override
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.findByStatus(Task.TaskStatus.COMPLETED).size();
        long overdueTasks = taskRepository.findOverdueTasks(LocalDate.now()).size();
        long pendingTasks = taskRepository.findByStatus(Task.TaskStatus.PENDING).size();

        stats.put("totalTasks", totalTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("overdueTasks", overdueTasks);
        stats.put("pendingTasks", pendingTasks);

        // Average completion rate
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0;
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        // Priority distribution
        for (Task.Priority priority : Task.Priority.values()) {
            long count = taskRepository.findByPriority(priority).size();
            stats.put(priority.name() + "PriorityTasks", count);
        }

        return stats;
    }

    @Override
    public Map<String, Object> getProjectCompletionRates() {
        Map<String, Object> completionRates = new HashMap<>();

        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            List<Task> projectTasks = taskRepository.findByProjectId(project.getId());
            if (!projectTasks.isEmpty()) {
                long completed = projectTasks.stream()
                        .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                        .count();
                double rate = (completed * 100.0) / projectTasks.size();
                completionRates.put(project.getName(), Math.round(rate * 100.0) / 100.0);
            }
        }

        return completionRates;
    }

    // ==================== LEAVE ANALYTICS ====================

    @Override
    public Map<String, Object> getLeaveStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Leave applications by status
        for (LeaveApplication.LeaveStatus status : LeaveApplication.LeaveStatus.values()) {
            long count = leaveApplicationRepository.findByStatus(status).size();
            stats.put(status.name() + "Leaves", count);
        }

        // Total leaves
        stats.put("totalLeaves", leaveApplicationRepository.count());

        // Average leave days (simplified)
        List<LeaveApplication> allLeaves = leaveApplicationRepository.findAll();
        double avgLeaveDays = allLeaves.stream()
                .mapToInt(LeaveApplication::getTotalDays)
                .average()
                .orElse(0.0);
        stats.put("averageLeaveDays", Math.round(avgLeaveDays * 100.0) / 100.0);

        return stats;
    }

    @Override
    public Map<String, Object> getLeaveTrend(int year) {
        Map<String, Object> trend = new HashMap<>();

        // Simplified - count leaves by year
        List<LeaveApplication> yearlyLeaves = leaveApplicationRepository.findAll().stream()
                .filter(leave -> leave.getStartDate().getYear() == year)
                .toList();

        trend.put("year", year);
        trend.put("totalLeaves", yearlyLeaves.size());
        trend.put("approvedLeaves", yearlyLeaves.stream()
                .filter(leave -> leave.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                .count());
        trend.put("pendingLeaves", yearlyLeaves.stream()
                .filter(leave -> leave.getStatus() == LeaveApplication.LeaveStatus.PENDING)
                .count());

        return trend;
    }

    // ==================== RECRUITMENT ANALYTICS ====================

    @Override
    public Map<String, Object> getRecruitmentStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalJobPostings", jobPostingRepository.count());
        stats.put("activeJobPostings", jobPostingRepository.findByStatus(JobPosting.JobStatus.OPEN).size());
        stats.put("totalCandidates", candidateRepository.count());
        stats.put("totalApplications", applicationRepository.count());

        // Application status distribution
        List<Object[]> applicationStatus = applicationRepository.countApplicationsByStatus();
        for (Object[] result : applicationStatus) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            stats.put(status + "Applications", count);
        }

        return stats;
    }

    // ==================== HELPER METHODS ====================

    private long getTotalEmployees() {
        return employeeRepository.count();
    }

    private Long getManagerDepartmentId(Long managerId) {
        // Simplified implementation - you should implement this properly
        return 1L; // Default department
    }
}