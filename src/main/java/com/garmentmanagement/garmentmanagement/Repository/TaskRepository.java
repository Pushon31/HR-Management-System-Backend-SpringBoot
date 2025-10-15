// Repository: TaskRepository.java
package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Employee specific
    List<Task> findByAssignedToId(Long employeeId);
    List<Task> findByAssignedToIdAndStatus(Long employeeId, Task.TaskStatus status);

    // Project specific
    List<Task> findByProjectId(Long projectId);
    List<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status);

    // Status based
    List<Task> findByStatus(Task.TaskStatus status);

    // Priority based
    List<Task> findByPriority(Task.Priority priority);

    // Department based
    @Query("SELECT t FROM Task t WHERE t.assignedTo.department.id = :departmentId")
    List<Task> findByDepartmentId(@Param("departmentId") Long departmentId);

    // Overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    // Urgent tasks
    List<Task> findByIsUrgentTrue();

    // Due date range
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    // Search by title
    List<Task> findByTitleContainingIgnoreCase(String title);

    // Count methods for dashboard
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :employeeId")
    Long countByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :employeeId AND t.status = 'COMPLETED'")
    Long countCompletedByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countTasksByStatus();

    // Upcoming deadlines
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findUpcomingTasks(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}