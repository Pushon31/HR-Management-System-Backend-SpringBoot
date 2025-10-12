package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Attendance;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // ✅ Keep these - they use Long employeeId (primary key)
    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);

    // ✅ Keep these - no employeeId parameter
    List<Attendance> findByAttendanceDate(LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month")
    List<Attendance> findByEmployeeIdAndMonth(Long employeeId, int year, int month);

    List<Attendance> findByStatus(Attendance.AttendanceStatus status);
    List<Attendance> findByAttendanceDateAndStatus(LocalDate date, Attendance.AttendanceStatus status);

    // ❌ REMOVE THIS - causing the error
    // Optional<Attendance> findByEmployeeId(String employeeId);
}