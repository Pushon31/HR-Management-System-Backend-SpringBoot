// DTO: TaskCommentDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long employeeId;
    private String employeeName;
    private String comment;
    private LocalDateTime commentDate;
    private Boolean isInternal;
}