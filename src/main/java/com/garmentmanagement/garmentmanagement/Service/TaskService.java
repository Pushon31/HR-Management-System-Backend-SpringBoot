// Service: TaskService.java
package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Entity.Task;
import com.garmentmanagement.garmentmanagement.Entity.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TaskService {

    // ==================== TASK MANAGEMENT METHODS ====================
    TaskDto createTask(TaskDto taskDto);
    TaskDto updateTask(Long id, TaskDto taskDto);
    TaskDto getTaskById(Long id);
    List<TaskDto> getAllTasks();
    List<TaskDto> getTasksByEmployee(Long employeeId);
    List<TaskDto> getTasksByProject(Long projectId);
    List<TaskDto> getTasksByStatus(Task.TaskStatus status);
    List<TaskDto> getTasksByPriority(Task.Priority priority);
    List<TaskDto> getOverdueTasks();
    List<TaskDto> getUrgentTasks();
    TaskDto updateTaskStatus(Long id, String status);
    TaskDto updateTaskProgress(Long id, Integer completionPercentage);
    void deleteTask(Long id);

    // ==================== PROJECT MANAGEMENT METHODS ====================
    ProjectDto createProject(ProjectDto projectDto);
    ProjectDto updateProject(Long id, ProjectDto projectDto);
    ProjectDto getProjectById(Long id);
    ProjectDto getProjectByCode(String code);
    List<ProjectDto> getAllProjects();
    List<ProjectDto> getProjectsByStatus(Project.ProjectStatus status);
    List<ProjectDto> getProjectsByDepartment(Long departmentId);
    void closeProject(Long id);

    // ==================== TASK COMMENTS & ATTACHMENTS ====================
    TaskCommentDto addComment(TaskCommentDto commentDto);
    List<TaskCommentDto> getTaskComments(Long taskId);
    TaskAttachmentDto addAttachment(TaskAttachmentDto attachmentDto);
    List<TaskAttachmentDto> getTaskAttachments(Long taskId);
    void deleteComment(Long commentId);
    void deleteAttachment(Long attachmentId);

    // ==================== DASHBOARD & ANALYTICS METHODS ====================
    Map<String, Object> getTaskDashboard();
    Map<String, Long> getTaskStatistics();
    Map<String, Long> getProjectStatistics();
    List<TaskDto> getUpcomingDeadlines(int days);
    List<TaskDto> searchTasks(String keyword);

    // ==================== ADMIN SPECIFIC METHODS ====================
    List<TaskDto> getTasksByDepartment(Long departmentId);
    List<TaskDto> getTasksByDateRange(LocalDate startDate, LocalDate endDate);
    TaskDto reassignTask(Long taskId, Long newEmployeeId);
    Map<String, Object> getEmployeePerformance(Long employeeId);


    // ==================== NEW METHODS FOR MANAGER & EMPLOYEE ====================

    // Manager specific methods
    List<TaskDto> getTasksByManager(Long managerId);
    List<TaskDto> getPendingTasksByManager(Long managerId);
    List<TaskDto> getOverdueTasksByManager(Long managerId);
    List<TaskDto> getCompletedTasksByManager(Long managerId);
    Map<String, Object> getManagerDashboard(Long managerId);
    List<TaskDto> getUpcomingDeadlinesByManager(Long managerId, int days);

    // Employee specific methods
    Map<String, Object> getEmployeeDashboard(Long employeeId);
    List<TaskDto> getUpcomingDeadlinesByEmployee(Long employeeId, int days);

    // Security validation methods
    boolean isTaskAssignedToEmployee(Long taskId, Long employeeId);
    boolean isTaskInManagerTeam(Long taskId, Long managerId);
    Long getManagerDepartmentId(Long managerId);
}