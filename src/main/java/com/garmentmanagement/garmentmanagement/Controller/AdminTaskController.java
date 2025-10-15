package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Entity.Task;
import com.garmentmanagement.garmentmanagement.Entity.Project;
import com.garmentmanagement.garmentmanagement.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final TaskService taskService;

    // ==================== TASK MANAGEMENT (FULL ACCESS) ====================

    /**
     * ✅ সব টাস্ক দেখা (সব ডিপার্টমেন্ট)
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ নতুন টাস্ক তৈরি করা (যেকোনো Employee-কে Assign করতে পারবেন)
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }

    /**
     * ✅ যে কোনো টাস্ক আপডেট করা
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * ✅ যে কোনো টাস্কের ডিটেইলস দেখা
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * ✅ নির্দিষ্ট Employee-এর সব টাস্ক দেখা
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskDto>> getTasksByEmployee(@PathVariable Long employeeId) {
        List<TaskDto> tasks = taskService.getTasksByEmployee(employeeId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ নির্দিষ্ট Department-এর সব টাস্ক দেখা
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<TaskDto>> getTasksByDepartment(@PathVariable Long departmentId) {
        List<TaskDto> tasks = taskService.getTasksByDepartment(departmentId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ Status অনুযায়ী টাস্ক ফিল্টার করা
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable String status) {
        Task.TaskStatus statusEnum = Task.TaskStatus.valueOf(status.toUpperCase());
        List<TaskDto> tasks = taskService.getTasksByStatus(statusEnum);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ Priority অনুযায়ী টাস্ক ফিল্টার করা
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable String priority) {
        Task.Priority priorityEnum = Task.Priority.valueOf(priority.toUpperCase());
        List<TaskDto> tasks = taskService.getTasksByPriority(priorityEnum);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ Overdue টাস্কগুলো দেখা
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        List<TaskDto> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ জরুরি টাস্কগুলো দেখা
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<TaskDto>> getUrgentTasks() {
        List<TaskDto> tasks = taskService.getUrgentTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ তারিখের Range অনুযায়ী টাস্ক দেখা
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<TaskDto>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TaskDto> tasks = taskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ টাস্ক Reassign করা (এক Employee থেকে অন্য Employee-তে)
     */
    @PutMapping("/{taskId}/reassign/{newEmployeeId}")
    public ResponseEntity<TaskDto> reassignTask(
            @PathVariable Long taskId,
            @PathVariable Long newEmployeeId) {
        TaskDto task = taskService.reassignTask(taskId, newEmployeeId);
        return ResponseEntity.ok(task);
    }

    /**
     * ✅ টাস্ক ডিলিট করা
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    // ==================== PROJECT MANAGEMENT (FULL ACCESS) ====================

    /**
     * ✅ সব প্রজেক্ট দেখা
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = taskService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * ✅ নতুন প্রজেক্ট তৈরি করা
     */
    @PostMapping("/projects")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        ProjectDto createdProject = taskService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }

    /**
     * ✅ প্রজেক্ট আপডেট করা
     */
    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDto projectDto) {
        ProjectDto updatedProject = taskService.updateProject(id, projectDto);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * ✅ প্রজেক্ট বন্ধ করা
     */
    @PutMapping("/projects/{id}/close")
    public ResponseEntity<Void> closeProject(@PathVariable Long id) {
        taskService.closeProject(id);
        return ResponseEntity.ok().build();
    }

    // ==================== DASHBOARD & ANALYTICS (ADMIN ONLY) ====================

    /**
     * ✅ এডমিন টাস্ক ড্যাশবোর্ড ডাটা
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getTaskDashboard() {
        Map<String, Object> dashboard = taskService.getTaskDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * ✅ টাস্ক স্ট্যাটিস্টিক্স
     */
    @GetMapping("/statistics/tasks")
    public ResponseEntity<Map<String, Long>> getTaskStatistics() {
        Map<String, Long> stats = taskService.getTaskStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ প্রজেক্ট স্ট্যাটিস্টিক্স
     */
    @GetMapping("/statistics/projects")
    public ResponseEntity<Map<String, Long>> getProjectStatistics() {
        Map<String, Long> stats = taskService.getProjectStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * ✅ Employee Performance রিপোর্ট
     */
    @GetMapping("/performance/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeePerformance(@PathVariable Long employeeId) {
        Map<String, Object> performance = taskService.getEmployeePerformance(employeeId);
        return ResponseEntity.ok(performance);
    }

    /**
     * ✅ আসন্ন ডেডলাইনের টাস্কগুলো
     */
    @GetMapping("/upcoming-deadlines")
    public ResponseEntity<List<TaskDto>> getUpcomingDeadlines(
            @RequestParam(defaultValue = "7") int days) {
        List<TaskDto> tasks = taskService.getUpcomingDeadlines(days);
        return ResponseEntity.ok(tasks);
    }

    /**
     * ✅ টাস্ক সার্চ করা
     */
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasks(@RequestParam String keyword) {
        List<TaskDto> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(tasks);
    }
}