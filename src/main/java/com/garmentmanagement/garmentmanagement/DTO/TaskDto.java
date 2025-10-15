// DTO: TaskDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long assignedToId;
    private String assignedToName;
    private Long assignedById;
    private String assignedByName;
    private Long projectId;
    private String projectName;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private LocalDate startDate;
    private LocalDate completedDate;
    private Double estimatedHours;
    private Double actualHours;
    private String tags;
    private Integer completionPercentage;
    private Boolean isUrgent;

    // Additional info
    private Integer commentCount;
    private Integer attachmentCount;
    private Boolean isOverdue;
    private String departmentName;
}