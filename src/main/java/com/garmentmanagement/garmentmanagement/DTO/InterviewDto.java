// DTO: InterviewDto.java
package com.garmentmanagement.garmentmanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDto {
    private Long id;
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private Long jobPostingId;
    private String jobTitle;
    private LocalDateTime interviewDate;
    private String interviewType;
    private Long interviewerId;
    private String interviewerName;
    private String location;
    private String agenda;
    private String status;
    private String feedback;
    private Integer rating;
    private String notes;
}