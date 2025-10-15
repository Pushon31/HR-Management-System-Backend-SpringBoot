// Service Implementation: TaskServiceImplementation.java
package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Entity.*;
import com.garmentmanagement.garmentmanagement.Repository.*;
import com.garmentmanagement.garmentmanagement.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImplementation implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    // ==================== TASK MANAGEMENT METHODS ====================

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        // Validate assigned employee
        Employee assignedTo = employeeRepository.findById(taskDto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + taskDto.getAssignedToId()));

        // Validate assigned by employee
        Employee assignedBy = employeeRepository.findById(taskDto.getAssignedById())
                .orElseThrow(() -> new RuntimeException("Assigner not found with id: " + taskDto.getAssignedById()));

        Task task = modelMapper.map(taskDto, Task.class);
        task.setAssignedTo(assignedTo);
        task.setAssignedBy(assignedBy);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setCompletionPercentage(0);

        // Set project if provided
        if (taskDto.getProjectId() != null) {
            Project project = projectRepository.findById(taskDto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + taskDto.getProjectId()));
            task.setProject(project);
        }

        // Set default start date if not provided
        if (task.getStartDate() == null) {
            task.setStartDate(LocalDate.now());
        }

        Task saved = taskRepository.save(task);
        return convertToTaskDto(saved);
    }

    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        modelMapper.map(taskDto, existing);

        // Update assigned employee if changed
        if (taskDto.getAssignedToId() != null &&
                !existing.getAssignedTo().getId().equals(taskDto.getAssignedToId())) {
            Employee assignedTo = employeeRepository.findById(taskDto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            existing.setAssignedTo(assignedTo);
        }

        // Update project if changed
        if (taskDto.getProjectId() != null) {
            Project project = projectRepository.findById(taskDto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            existing.setProject(project);
        } else {
            existing.setProject(null);
        }

        // Update enums
        if (taskDto.getPriority() != null) {
            existing.setPriority(Task.Priority.valueOf(taskDto.getPriority()));
        }
        if (taskDto.getStatus() != null) {
            existing.setStatus(Task.TaskStatus.valueOf(taskDto.getStatus()));
        }

        // Auto-complete if 100%
        if (existing.getCompletionPercentage() == 100) {
            existing.setStatus(Task.TaskStatus.COMPLETED);
            existing.setCompletedDate(LocalDate.now());
        }

        // Check if overdue
        if (existing.getDueDate() != null &&
                existing.getDueDate().isBefore(LocalDate.now()) &&
                existing.getStatus() != Task.TaskStatus.COMPLETED &&
                existing.getStatus() != Task.TaskStatus.CANCELLED) {
            existing.setStatus(Task.TaskStatus.OVERDUE);
        }

        Task updated = taskRepository.save(existing);
        return convertToTaskDto(updated);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return convertToTaskDto(task);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByEmployee(Long employeeId) {
        return taskRepository.findByAssignedToId(employeeId)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now())
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getUrgentTasks() {
        return taskRepository.findByIsUrgentTrue()
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto updateTaskStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus(Task.TaskStatus.valueOf(status));

        // Set completed date if status is COMPLETED
        if (status.equals("COMPLETED")) {
            task.setCompletedDate(LocalDate.now());
            task.setCompletionPercentage(100);
        }

        Task updated = taskRepository.save(task);
        return convertToTaskDto(updated);
    }

    @Override
    public TaskDto updateTaskProgress(Long id, Integer completionPercentage) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setCompletionPercentage(completionPercentage);

        // Auto-update status based on progress
        if (completionPercentage == 100) {
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompletedDate(LocalDate.now());
        } else if (completionPercentage > 0) {
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
        }

        Task updated = taskRepository.save(task);
        return convertToTaskDto(updated);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    // ==================== PROJECT MANAGEMENT METHODS ====================

    @Override
    public ProjectDto createProject(ProjectDto projectDto) {
        // Check duplicate project code
        if (projectRepository.existsByCode(projectDto.getCode())) {
            throw new RuntimeException("Project code already exists: " + projectDto.getCode());
        }

        Project project = modelMapper.map(projectDto, Project.class);

        // Set department if provided
        if (projectDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(projectDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            project.setDepartment(department);
        }

        // Set project manager if provided
        if (projectDto.getProjectManagerId() != null) {
            Employee projectManager = employeeRepository.findById(projectDto.getProjectManagerId())
                    .orElseThrow(() -> new RuntimeException("Project manager not found"));
            project.setProjectManager(projectManager);
        }

        project.setStatus(Project.ProjectStatus.PLANNING);

        Project saved = projectRepository.save(project);
        return convertToProjectDto(saved);
    }

    @Override
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        // Check duplicate project code
        if (!existing.getCode().equals(projectDto.getCode()) &&
                projectRepository.existsByCode(projectDto.getCode())) {
            throw new RuntimeException("Project code already exists: " + projectDto.getCode());
        }

        modelMapper.map(projectDto, existing);

        // Update department if changed
        if (projectDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(projectDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existing.setDepartment(department);
        }

        // Update project manager if changed
        if (projectDto.getProjectManagerId() != null) {
            Employee projectManager = employeeRepository.findById(projectDto.getProjectManagerId())
                    .orElseThrow(() -> new RuntimeException("Project manager not found"));
            existing.setProjectManager(projectManager);
        }

        // Update enum status
        if (projectDto.getStatus() != null) {
            existing.setStatus(Project.ProjectStatus.valueOf(projectDto.getStatus()));
        }

        Project updated = projectRepository.save(existing);
        return convertToProjectDto(updated);
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return convertToProjectDto(project);
    }

    @Override
    public ProjectDto getProjectByCode(String code) {
        Project project = projectRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Project not found with code: " + code));
        return convertToProjectDto(project);
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::convertToProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status)
                .stream()
                .map(this::convertToProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByDepartment(Long departmentId) {
        return projectRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    public void closeProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        project.setStatus(Project.ProjectStatus.COMPLETED);
        projectRepository.save(project);
    }

    // ==================== TASK COMMENTS & ATTACHMENTS ====================

    @Override
    public TaskCommentDto addComment(TaskCommentDto commentDto) {
        Task task = taskRepository.findById(commentDto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Employee employee = employeeRepository.findById(commentDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        TaskComment comment = modelMapper.map(commentDto, TaskComment.class);
        comment.setTask(task);
        comment.setEmployee(employee);
        comment.setCommentDate(LocalDateTime.now());

        TaskComment saved = taskCommentRepository.save(comment);
        return convertToTaskCommentDto(saved);
    }

    @Override
    public List<TaskCommentDto> getTaskComments(Long taskId) {
        return taskCommentRepository.findByTaskIdOrderByCommentDateDesc(taskId)
                .stream()
                .map(this::convertToTaskCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskAttachmentDto addAttachment(TaskAttachmentDto attachmentDto) {
        Task task = taskRepository.findById(attachmentDto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Employee uploadedBy = employeeRepository.findById(attachmentDto.getUploadedById())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        TaskAttachment attachment = modelMapper.map(attachmentDto, TaskAttachment.class);
        attachment.setTask(task);
        attachment.setUploadedBy(uploadedBy);
        attachment.setUploadDate(LocalDateTime.now());

        TaskAttachment saved = taskAttachmentRepository.save(attachment);
        return convertToTaskAttachmentDto(saved);
    }

    @Override
    public List<TaskAttachmentDto> getTaskAttachments(Long taskId) {
        return taskAttachmentRepository.findByTaskId(taskId)
                .stream()
                .map(this::convertToTaskAttachmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        taskCommentRepository.delete(comment);
    }

    @Override
    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        taskAttachmentRepository.delete(attachment);
    }

    // ==================== DASHBOARD & ANALYTICS METHODS ====================

    @Override
    public Map<String, Object> getTaskDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // Basic counts
        dashboard.put("totalTasks", taskRepository.count());
        dashboard.put("completedTasks", taskRepository.countCompletedByEmployee(null)); // Need to fix this
        dashboard.put("overdueTasks", taskRepository.findOverdueTasks(LocalDate.now()).size());
        dashboard.put("urgentTasks", taskRepository.findByIsUrgentTrue().size());

        // Status distribution
        List<Object[]> statusCounts = taskRepository.countTasksByStatus();
        Map<String, Long> statusStats = new HashMap<>();
        for (Object[] result : statusCounts) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            statusStats.put(status, count);
        }
        dashboard.put("statusDistribution", statusStats);

        // Priority distribution
        long highPriority = taskRepository.findByPriority(Task.Priority.HIGH).size();
        long urgentPriority = taskRepository.findByPriority(Task.Priority.URGENT).size();
        dashboard.put("highPriorityTasks", highPriority);
        dashboard.put("urgentPriorityTasks", urgentPriority);

        // Recent activities (last 7 days tasks)
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        List<Task> recentTasks = taskRepository.findByDueDateBetween(weekAgo, LocalDate.now());
        dashboard.put("recentTasks", recentTasks.size());

        return dashboard;
    }

    @Override
    public Map<String, Long> getTaskStatistics() {
        Map<String, Long> stats = new HashMap<>();

        List<Object[]> statusCounts = taskRepository.countTasksByStatus();
        for (Object[] result : statusCounts) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            stats.put(status, count);
        }

        // Add priority counts
        for (Task.Priority priority : Task.Priority.values()) {
            Long count = (long) taskRepository.findByPriority(priority).size();
            stats.put(priority.name() + "_PRIORITY", count);
        }

        return stats;
    }

    @Override
    public Map<String, Long> getProjectStatistics() {
        Map<String, Long> stats = new HashMap<>();

        List<Object[]> statusCounts = projectRepository.countProjectsByStatus();
        for (Object[] result : statusCounts) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            stats.put(status, count);
        }

        stats.put("TOTAL_PROJECTS", projectRepository.count());
        stats.put("ACTIVE_PROJECTS", projectRepository.countActiveProjects());

        return stats;
    }

    @Override
    public List<TaskDto> getUpcomingDeadlines(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);

        return taskRepository.findUpcomingTasks(startDate, endDate)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> searchTasks(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    // ==================== ADMIN SPECIFIC METHODS ====================

    @Override
    public List<TaskDto> getTasksByDepartment(Long departmentId) {
        return taskRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByDueDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto reassignTask(Long taskId, Long newEmployeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Employee newEmployee = employeeRepository.findById(newEmployeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        task.setAssignedTo(newEmployee);
        Task updated = taskRepository.save(task);
        return convertToTaskDto(updated);
    }

    @Override
    public Map<String, Object> getEmployeePerformance(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Map<String, Object> performance = new HashMap<>();

        List<Task> employeeTasks = taskRepository.findByAssignedToId(employeeId);
        long totalTasks = employeeTasks.size();
        long completedTasks = employeeTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count();
        long overdueTasks = employeeTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.OVERDUE)
                .count();

        performance.put("employeeName", employee.getFirstName() + " " + employee.getLastName());
        performance.put("totalTasks", totalTasks);
        performance.put("completedTasks", completedTasks);
        performance.put("overdueTasks", overdueTasks);
        performance.put("completionRate", totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0);
        performance.put("department", employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A");

        return performance;
    }

    // ==================== HELPER CONVERSION METHODS ====================

    private TaskDto convertToTaskDto(Task task) {
        TaskDto dto = modelMapper.map(task, TaskDto.class);

        // Employee info
        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToName(task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName());

            if (task.getAssignedTo().getDepartment() != null) {
                dto.setDepartmentName(task.getAssignedTo().getDepartment().getName());
            }
        }

        if (task.getAssignedBy() != null) {
            dto.setAssignedById(task.getAssignedBy().getId());
            dto.setAssignedByName(task.getAssignedBy().getFirstName() + " " + task.getAssignedBy().getLastName());
        }

        // Project info
        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
            dto.setProjectName(task.getProject().getName());
        }

        // Enum conversions
        if (task.getPriority() != null) {
            dto.setPriority(task.getPriority().name());
        }
        if (task.getStatus() != null) {
            dto.setStatus(task.getStatus().name());
        }

        // Additional info
        Long commentCount = taskCommentRepository.countByTaskId(task.getId());
        Long attachmentCount = taskAttachmentRepository.countByTaskId(task.getId());
        dto.setCommentCount(commentCount.intValue());
        dto.setAttachmentCount(attachmentCount.intValue());

        // Check if overdue
        boolean isOverdue = task.getDueDate() != null &&
                task.getDueDate().isBefore(LocalDate.now()) &&
                task.getStatus() != Task.TaskStatus.COMPLETED &&
                task.getStatus() != Task.TaskStatus.CANCELLED;
        dto.setIsOverdue(isOverdue);

        return dto;
    }

    private ProjectDto convertToProjectDto(Project project) {
        ProjectDto dto = modelMapper.map(project, ProjectDto.class);

        // Department info
        if (project.getDepartment() != null) {
            dto.setDepartmentId(project.getDepartment().getId());
            dto.setDepartmentName(project.getDepartment().getName());
        }

        // Project manager info
        if (project.getProjectManager() != null) {
            dto.setProjectManagerId(project.getProjectManager().getId());
            dto.setProjectManagerName(project.getProjectManager().getFirstName() + " " +
                    project.getProjectManager().getLastName());
        }

        // Enum conversion
        if (project.getStatus() != null) {
            dto.setStatus(project.getStatus().name());
        }

        // Statistics
        List<Task> projectTasks = taskRepository.findByProjectId(project.getId());
        dto.setTotalTasks(projectTasks.size());
        dto.setCompletedTasks((int) projectTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count());
        dto.setPendingTasks((int) projectTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.PENDING ||
                        task.getStatus() == Task.TaskStatus.IN_PROGRESS)
                .count());
        dto.setOverdueTasks((int) projectTasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.OVERDUE)
                .count());

        // Completion rate
        dto.setCompletionRate(projectTasks.size() > 0 ?
                (dto.getCompletedTasks() * 100.0 / projectTasks.size()) : 0);

        return dto;
    }

    private TaskCommentDto convertToTaskCommentDto(TaskComment comment) {
        TaskCommentDto dto = modelMapper.map(comment, TaskCommentDto.class);

        if (comment.getTask() != null) {
            dto.setTaskId(comment.getTask().getId());
            dto.setTaskTitle(comment.getTask().getTitle());
        }

        if (comment.getEmployee() != null) {
            dto.setEmployeeId(comment.getEmployee().getId());
            dto.setEmployeeName(comment.getEmployee().getFirstName() + " " + comment.getEmployee().getLastName());
        }

        return dto;
    }

    private TaskAttachmentDto convertToTaskAttachmentDto(TaskAttachment attachment) {
        TaskAttachmentDto dto = modelMapper.map(attachment, TaskAttachmentDto.class);

        if (attachment.getTask() != null) {
            dto.setTaskId(attachment.getTask().getId());
            dto.setTaskTitle(attachment.getTask().getTitle());
        }

        if (attachment.getUploadedBy() != null) {
            dto.setUploadedById(attachment.getUploadedBy().getId());
            dto.setUploadedByName(attachment.getUploadedBy().getFirstName() + " " + attachment.getUploadedBy().getLastName());
        }

        return dto;
    }



    @Override
    public List<TaskDto> getTasksByManager(Long managerId) {
        Long departmentId = getManagerDepartmentId(managerId);
        return taskRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getPendingTasksByManager(Long managerId) {
        return getTasksByManager(managerId).stream()
                .filter(task -> "PENDING".equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getOverdueTasksByManager(Long managerId) {
        return getTasksByManager(managerId).stream()
                .filter(TaskDto::getIsOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getCompletedTasksByManager(Long managerId) {
        return getTasksByManager(managerId).stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getManagerDashboard(Long managerId) {
        List<TaskDto> teamTasks = getTasksByManager(managerId);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalTeamTasks", teamTasks.size());
        dashboard.put("completedTeamTasks", teamTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count());
        dashboard.put("pendingTeamTasks", teamTasks.stream()
                .filter(task -> "PENDING".equals(task.getStatus()))
                .count());
        dashboard.put("overdueTeamTasks", teamTasks.stream()
                .filter(TaskDto::getIsOverdue)
                .count());
        dashboard.put("urgentTeamTasks", teamTasks.stream()
                .filter(task -> Boolean.TRUE.equals(task.getIsUrgent()))
                .count());

        return dashboard;
    }

    @Override
    public List<TaskDto> getUpcomingDeadlinesByManager(Long managerId, int days) {
        List<TaskDto> teamTasks = getTasksByManager(managerId);
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(days);

        return teamTasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> {
                    LocalDate dueDate = task.getDueDate();
                    return (dueDate.isAfter(today) || dueDate.isEqual(today)) &&
                            dueDate.isBefore(deadline) &&
                            !"COMPLETED".equals(task.getStatus());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getEmployeeDashboard(Long employeeId) {
        List<TaskDto> myTasks = getTasksByEmployee(employeeId);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalTasks", myTasks.size());
        dashboard.put("completedTasks", myTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count());
        dashboard.put("pendingTasks", myTasks.stream()
                .filter(task -> "PENDING".equals(task.getStatus()))
                .count());
        dashboard.put("inProgressTasks", myTasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .count());
        dashboard.put("overdueTasks", myTasks.stream()
                .filter(TaskDto::getIsOverdue)
                .count());
        dashboard.put("urgentTasks", myTasks.stream()
                .filter(task -> Boolean.TRUE.equals(task.getIsUrgent()))
                .count());

        double avgCompletion = myTasks.stream()
                .mapToInt(task -> task.getCompletionPercentage() != null ? task.getCompletionPercentage() : 0)
                .average()
                .orElse(0.0);
        dashboard.put("averageCompletion", avgCompletion);

        return dashboard;
    }

    @Override
    public List<TaskDto> getUpcomingDeadlinesByEmployee(Long employeeId, int days) {
        List<TaskDto> myTasks = getTasksByEmployee(employeeId);
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(days);

        return myTasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> {
                    LocalDate dueDate = task.getDueDate();
                    return (dueDate.isAfter(today) || dueDate.isEqual(today)) &&
                            dueDate.isBefore(deadline) &&
                            !"COMPLETED".equals(task.getStatus());
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTaskAssignedToEmployee(Long taskId, Long employeeId) {
        try {
            TaskDto task = getTaskById(taskId);
            return task.getAssignedToId() != null && task.getAssignedToId().equals(employeeId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isTaskInManagerTeam(Long taskId, Long managerId) {
        try {
            TaskDto task = getTaskById(taskId);
            Long taskDepartmentId = getDepartmentIdFromTask(task);
            Long managerDepartmentId = getManagerDepartmentId(managerId);
            return taskDepartmentId != null && taskDepartmentId.equals(managerDepartmentId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long getManagerDepartmentId(Long managerId) {
        // Implementation: Get manager's department from Employee entity
        // This should be implemented based on your Employee-Department relationship
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return manager.getDepartment() != null ? manager.getDepartment().getId() : null;
    }

    // Helper method
    private Long getDepartmentIdFromTask(TaskDto task) {
        // This should extract department ID from task
        // You might need to adjust based on your Task-Employee-Department relationship
        if (task.getAssignedToId() != null) {
            Employee employee = employeeRepository.findById(task.getAssignedToId())
                    .orElse(null);
            return employee != null && employee.getDepartment() != null ?
                    employee.getDepartment().getId() : null;
        }
        return null;
    }
}