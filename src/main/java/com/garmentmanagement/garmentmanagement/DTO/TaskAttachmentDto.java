// DTO: TaskAttachmentDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadDate;
    private String description;
}