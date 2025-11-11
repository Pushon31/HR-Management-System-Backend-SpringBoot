package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee/tasks")
@RequiredArgsConstructor
public class EmployeeTaskController {

    private final TaskService taskService;

    // ==================== EMPLOYEE'S OWN TASKS ====================


    @GetMapping("/my-tasks/{employeeId}")
    public ResponseEntity<List<TaskDto>> getMyTasks(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId);
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/pending")
    public ResponseEntity<List<TaskDto>> getMyPendingTasks(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId)
                .stream()
                .filter(task -> "PENDING".equals(task.getStatus()))
                .toList();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/in-progress")
    public ResponseEntity<List<TaskDto>> getMyInProgressTasks(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId)
                .stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .toList();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/completed")
    public ResponseEntity<List<TaskDto>> getMyCompletedTasks(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId)
                .stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .toList();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/overdue")
    public ResponseEntity<List<TaskDto>> getMyOverdueTasks(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId)
                .stream()
                .filter(TaskDto::getIsOverdue)
                .toList();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/urgent")
    public ResponseEntity<List<TaskDto>> getMyUrgentTasks(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId)
                .stream()
                .filter(task -> Boolean.TRUE.equals(task.getIsUrgent()) ||
                        "URGENT".equals(task.getPriority()) ||
                        "HIGH".equals(task.getPriority()))
                .toList();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/{taskId}")
    public ResponseEntity<TaskDto> getMyTaskDetails(
            @PathVariable Long employeeId,
            @PathVariable Long taskId) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            TaskDto task = taskService.getTaskById(taskId);
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    // ==================== TASK ACTIONS (EMPLOYEE) ====================


    @PutMapping("/my-tasks/{employeeId}/{taskId}/progress")
    public ResponseEntity<TaskDto> updateMyTaskProgress(
            @PathVariable Long employeeId,
            @PathVariable Long taskId,
            @RequestParam Integer completionPercentage) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            TaskDto updatedTask = taskService.updateTaskProgress(taskId, completionPercentage);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.status(403).build();
        }
    }


    @PutMapping("/my-tasks/{employeeId}/{taskId}/status")
    public ResponseEntity<TaskDto> updateMyTaskStatus(
            @PathVariable Long employeeId,
            @PathVariable Long taskId,
            @RequestParam String status) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            TaskDto updatedTask = taskService.updateTaskStatus(taskId, status);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.status(403).build();
        }
    }


    @PostMapping("/my-tasks/{employeeId}/{taskId}/comments")
    public ResponseEntity<TaskCommentDto> addCommentToMyTask(
            @PathVariable Long employeeId,
            @PathVariable Long taskId,
            @RequestBody TaskCommentDto commentDto) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            commentDto.setEmployeeId(employeeId);
            commentDto.setTaskId(taskId);
            TaskCommentDto comment = taskService.addComment(commentDto);
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(403).build();
        }
    }


    @PostMapping("/my-tasks/{employeeId}/{taskId}/attachments")
    public ResponseEntity<TaskAttachmentDto> addAttachmentToMyTask(
            @PathVariable Long employeeId,
            @PathVariable Long taskId,
            @RequestBody TaskAttachmentDto attachmentDto) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            attachmentDto.setUploadedById(employeeId);
            attachmentDto.setTaskId(taskId);
            TaskAttachmentDto attachment = taskService.addAttachment(attachmentDto);
            return ResponseEntity.ok(attachment);
        } else {
            return ResponseEntity.status(403).build();
        }
    }


    @GetMapping("/my-tasks/{employeeId}/{taskId}/comments")
    public ResponseEntity<List<TaskCommentDto>> getMyTaskComments(
            @PathVariable Long employeeId,
            @PathVariable Long taskId) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            List<TaskCommentDto> comments = taskService.getTaskComments(taskId);
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.status(403).build();
        }
    }


    @GetMapping("/my-tasks/{employeeId}/{taskId}/attachments")
    public ResponseEntity<List<TaskAttachmentDto>> getMyTaskAttachments(
            @PathVariable Long employeeId,
            @PathVariable Long taskId) {
        // Use service method for security check
        if (taskService.isTaskAssignedToEmployee(taskId, employeeId)) {
            List<TaskAttachmentDto> attachments = taskService.getTaskAttachments(taskId);
            return ResponseEntity.ok(attachments);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // ==================== EMPLOYEE DASHBOARD ====================


    @GetMapping("/dashboard/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard(@PathVariable Long employeeId) {
        Map<String, Object> dashboard = taskService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(dashboard);
    }


    @GetMapping("/performance/{employeeId}")
    public ResponseEntity<Map<String, Object>> getMyPerformance(@PathVariable Long employeeId) {
        Map<String, Object> performance = taskService.getEmployeePerformance(employeeId);
        return ResponseEntity.ok(performance);
    }


    @GetMapping("/my-tasks/{employeeId}/upcoming-deadlines")
    public ResponseEntity<List<TaskDto>> getMyUpcomingDeadlines(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "7") int days) {
        List<TaskDto> tasks = taskService.getUpcomingDeadlinesByEmployee(employeeId, days);
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/my-tasks/{employeeId}/search")
    public ResponseEntity<List<TaskDto>> searchMyTasks(
            @PathVariable Long employeeId,
            @RequestParam String keyword) {
        List<TaskDto> myTasks = taskService.getTasksByEmployee(employeeId);
        List<TaskDto> filteredTasks = myTasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        (task.getDescription() != null &&
                                task.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
        return ResponseEntity.ok(filteredTasks);
    }


    @GetMapping("/my-tasks/{employeeId}/statistics")
    public ResponseEntity<Map<String, Object>> getMyTaskStatistics(@PathVariable Long employeeId) {
        List<TaskDto> myTasks = taskService.getTasksByEmployee(employeeId);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalTasks", myTasks.size());
        stats.put("completedTasks", myTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count());
        stats.put("pendingTasks", myTasks.stream()
                .filter(task -> "PENDING".equals(task.getStatus()))
                .count());
        stats.put("inProgressTasks", myTasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .count());
        stats.put("overdueTasks", myTasks.stream()
                .filter(TaskDto::getIsOverdue)
                .count());

        // Calculate completion rate
        double completionRate = myTasks.isEmpty() ? 0.0 :
                (double) myTasks.stream()
                        .filter(task -> "COMPLETED".equals(task.getStatus()))
                        .count() / myTasks.size() * 100;
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        return ResponseEntity.ok(stats);
    }
}