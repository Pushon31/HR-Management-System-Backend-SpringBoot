package com.garmentmanagement.garmentmanagement.Entity;

import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "attendances")
public class Attendance extends BaseEntity {

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, HALF_DAY
    }
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;
    @Column(nullable = false)
    private LocalDate attendanceDate;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private String remarks;
    @Column(name = "total_hours")
    private Double totalHours;

}
