package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/tasks")
@RequiredArgsConstructor
public class ManagerTaskController {

    private final TaskService taskService;

    // ==================== MANAGER'S TEAM TASKS ====================

    /**
     * ✅ নিজের টিমের সব টাস্ক দেখা
     */
    @GetMapping("/my-team/{managerId}")
    public ResponseEntity<List<TaskDto>> getMyTeamTasks(@PathVariable Long managerId) {
        List<TaskDto> tasks = taskService.getTasksByManager(managerId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ নিজের টিমের Pending টাস্কগুলো দেখা
     */
    @GetMapping("/my-team/{managerId}/pending")
    public ResponseEntity<List<TaskDto>> getMyTeamPendingTasks(@PathVariable Long managerId) {
        List<TaskDto> tasks = taskService.getPendingTasksByManager(managerId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ নিজের টিমের In Progress টাস্কগুলো দেখা
     */
    @GetMapping("/my-team/{managerId}/in-progress")
    public ResponseEntity<List<TaskDto>> getMyTeamInProgressTasks(@PathVariable Long managerId) {
        List<TaskDto> allTeamTasks = taskService.getTasksByManager(managerId);
        List<TaskDto> inProgressTasks = allTeamTasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .toList();
        return ResponseEntity.ok(inProgressTasks);
    }

    /**
     * ✅ নিজের টিমের Overdue টাস্কগুলো দেখা
     */
    @GetMapping("/my-team/{managerId}/overdue")
    public ResponseEntity<List<TaskDto>> getMyTeamOverdueTasks(@PathVariable Long managerId) {
        List<TaskDto> tasks = taskService.getOverdueTasksByManager(managerId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ নিজের টিমের Completed টাস্কগুলো দেখা
     */
    @GetMapping("/my-team/{managerId}/completed")
    public ResponseEntity<List<TaskDto>> getMyTeamCompletedTasks(@PathVariable Long managerId) {
        List<TaskDto> tasks = taskService.getCompletedTasksByManager(managerId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ নিজের টিমের জরুরি টাস্কগুলো দেখা
     */
    @GetMapping("/my-team/{managerId}/urgent")
    public ResponseEntity<List<TaskDto>> getMyTeamUrgentTasks(@PathVariable Long managerId) {
        List<TaskDto> allTeamTasks = taskService.getTasksByManager(managerId);
        List<TaskDto> urgentTasks = allTeamTasks.stream()
                .filter(task -> Boolean.TRUE.equals(task.getIsUrgent()) ||
                        "URGENT".equals(task.getPriority()) ||
                        "HIGH".equals(task.getPriority()))
                .toList();
        return ResponseEntity.ok(urgentTasks);
    }

    /**
     * ✅ নিজের টিমের সদস্যদের Performance দেখা
     */
    @GetMapping("/my-team/{managerId}/performance")
    public ResponseEntity<Map<String, Object>> getMyTeamPerformance(@PathVariable Long managerId) {
        // This would aggregate performance of all team members
        // For now, returning manager's own performance as placeholder
        Map<String, Object> performance = taskService.getEmployeePerformance(managerId);
        return ResponseEntity.ok(performance);
    }

    /**
     * ✅ নিজের টিমের সদস্যকে নতুন টাস্ক Assign করা
     */
    @PostMapping("/my-team/{managerId}/assign")
    public ResponseEntity<TaskDto> assignTaskToTeamMember(
            @PathVariable Long managerId,
            @RequestBody TaskDto taskDto) {
        // Set the assignedBy to managerId
        taskDto.setAssignedById(managerId);

        // Verify that the assigned employee is in manager's team
        if (isEmployeeInManagerTeam(taskDto.getAssignedToId(), managerId)) {
            TaskDto createdTask = taskService.createTask(taskDto);
            return ResponseEntity.ok(createdTask);
        } else {
            return ResponseEntity.status(403).build(); // Forbidden - Employee not in team
        }
    }

    /**
     * ✅ নিজের টিমের টাস্ক আপডেট করা
     */
    @PutMapping("/my-team/{managerId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> updateTeamTask(
            @PathVariable Long managerId,
            @PathVariable Long taskId,
            @RequestBody TaskDto taskDto) {
        // First verify the task belongs to manager's team
        if (taskService.isTaskInManagerTeam(taskId, managerId)) {
            TaskDto updatedTask = taskService.updateTask(taskId, taskDto);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    /**
     * ✅ নিজের টিমের টাস্কের Status আপডেট করা
     */
    @PutMapping("/my-team/{managerId}/tasks/{taskId}/status")
    public ResponseEntity<TaskDto> updateTeamTaskStatus(
            @PathVariable Long managerId,
            @PathVariable Long taskId,
            @RequestParam String status) {
        if (taskService.isTaskInManagerTeam(taskId, managerId)) {
            TaskDto updatedTask = taskService.updateTaskStatus(taskId, status);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * ✅ নিজের টিমের টাস্কে কমেন্ট করা
     */
    @PostMapping("/my-team/{managerId}/tasks/{taskId}/comments")
    public ResponseEntity<TaskCommentDto> addCommentToTeamTask(
            @PathVariable Long managerId,
            @PathVariable Long taskId,
            @RequestBody TaskCommentDto commentDto) {
        if (taskService.isTaskInManagerTeam(taskId, managerId)) {
            commentDto.setEmployeeId(managerId);
            commentDto.setTaskId(taskId);
            TaskCommentDto comment = taskService.addComment(commentDto);
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * ✅ নিজের টিমের টাস্কে ফাইল আপলোড করা
     */
    @PostMapping("/my-team/{managerId}/tasks/{taskId}/attachments")
    public ResponseEntity<TaskAttachmentDto> addAttachmentToTeamTask(
            @PathVariable Long managerId,
            @PathVariable Long taskId,
            @RequestBody TaskAttachmentDto attachmentDto) {
        if (taskService.isTaskInManagerTeam(taskId, managerId)) {
            attachmentDto.setUploadedById(managerId);
            attachmentDto.setTaskId(taskId);
            TaskAttachmentDto attachment = taskService.addAttachment(attachmentDto);
            return ResponseEntity.ok(attachment);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // ==================== MANAGER'S PROJECTS ====================

    /**
     * ✅ নিজের টিমের প্রজেক্টগুলো দেখা
     */
    @GetMapping("/my-team/{managerId}/projects")
    public ResponseEntity<List<ProjectDto>> getMyTeamProjects(@PathVariable Long managerId) {
        Long departmentId = taskService.getManagerDepartmentId(managerId);
        List<ProjectDto> projects = taskService.getProjectsByDepartment(departmentId);
        return ResponseEntity.ok(projects);
    }

    /**
     * ✅ নিজের টিমের জন্য নতুন প্রজেক্ট তৈরি করা
     */
    @PostMapping("/my-team/{managerId}/projects")
    public ResponseEntity<ProjectDto> createProjectForTeam(
            @PathVariable Long managerId,
            @RequestBody ProjectDto projectDto) {
        projectDto.setProjectManagerId(managerId);
        projectDto.setDepartmentId(taskService.getManagerDepartmentId(managerId));
        ProjectDto createdProject = taskService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }

    // ==================== MANAGER DASHBOARD ====================

    /**
     * ✅ ম্যানেজার ড্যাশবোর্ড ডাটা
     */
    @GetMapping("/dashboard/{managerId}")
    public ResponseEntity<Map<String, Object>> getManagerDashboard(@PathVariable Long managerId) {
        Map<String, Object> dashboard = taskService.getManagerDashboard(managerId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * ✅ আসন্ন ডেডলাইনের টাস্কগুলো (টিমের)
     */
    @GetMapping("/my-team/{managerId}/upcoming-deadlines")
    public ResponseEntity<List<TaskDto>> getTeamUpcomingDeadlines(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "7") int days) {
        List<TaskDto> tasks = taskService.getUpcomingDeadlinesByManager(managerId, days);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ টিমের টাস্ক স্ট্যাটিস্টিক্স
     */
    @GetMapping("/my-team/{managerId}/statistics")
    public ResponseEntity<Map<String, Object>> getTeamStatistics(@PathVariable Long managerId) {
        List<TaskDto> teamTasks = taskService.getTasksByManager(managerId);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalTasks", teamTasks.size());
        stats.put("completedTasks", teamTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count());
        stats.put("pendingTasks", teamTasks.stream()
                .filter(task -> "PENDING".equals(task.getStatus()))
                .count());
        stats.put("inProgressTasks", teamTasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .count());
        stats.put("overdueTasks", teamTasks.stream()
                .filter(task -> Boolean.TRUE.equals(task.getIsOverdue()))
                .count());

        // Priority breakdown
        stats.put("urgentTasks", teamTasks.stream()
                .filter(task -> "URGENT".equals(task.getPriority()))
                .count());
        stats.put("highPriorityTasks", teamTasks.stream()
                .filter(task -> "HIGH".equals(task.getPriority()))
                .count());
        stats.put("mediumPriorityTasks", teamTasks.stream()
                .filter(task -> "MEDIUM".equals(task.getPriority()))
                .count());
        stats.put("lowPriorityTasks", teamTasks.stream()
                .filter(task -> "LOW".equals(task.getPriority()))
                .count());

        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ টিমের টাস্ক সার্চ করা
     */
    @GetMapping("/my-team/{managerId}/search")
    public ResponseEntity<List<TaskDto>> searchTeamTasks(
            @PathVariable Long managerId,
            @RequestParam String keyword) {
        List<TaskDto> teamTasks = taskService.getTasksByManager(managerId);
        List<TaskDto> filteredTasks = teamTasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        (task.getDescription() != null &&
                                task.getDescription().toLowerCase().contains(keyword.toLowerCase())) ||
                        (task.getAssignedToName() != null &&
                                task.getAssignedToName().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
        return ResponseEntity.ok(filteredTasks);
    }

    // ==================== HELPER METHODS ====================

    /**
     * ✅ Helper method: Check if employee is in manager's team
     */
    private boolean isEmployeeInManagerTeam(Long employeeId, Long managerId) {
        try {
            // Get manager's department
            Long managerDepartmentId = taskService.getManagerDepartmentId(managerId);

            // Get employee's department
            // This should be implemented based on your Employee entity
            // For now, assuming we can get it from task service
            // You might need to add a method in EmployeeService to get employee department
            return true; // Placeholder - implement based on your business logic
        } catch (Exception e) {
            return false;
        }
    }
}