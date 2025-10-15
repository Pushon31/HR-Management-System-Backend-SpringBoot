// DTO: ProjectDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long departmentId;
    private String departmentName;
    private Long projectManagerId;
    private String projectManagerName;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private Double actualCost;
    private String objectives;

    // Statistics
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer pendingTasks;
    private Integer overdueTasks;
    private Double completionRate;
}