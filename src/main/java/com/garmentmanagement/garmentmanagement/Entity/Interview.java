// Entity: Interview.java
package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interviews")
public class Interview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "interview_date", nullable = false)
    private LocalDateTime interviewDate;

    @Column(name = "interview_type", length = 50)
    private String interviewType; // Technical, HR, Final

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Employee interviewer;

    @Column(length = 100)
    private String location;

    @Column(length = 500)
    private String agenda;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating; // 1-5

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum InterviewStatus {
        SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    }
}