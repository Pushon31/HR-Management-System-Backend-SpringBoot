package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.AttendanceDto;
import com.garmentmanagement.garmentmanagement.Entity.Attendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    // ==================== BASIC CRUD OPERATIONS ====================
    AttendanceDto checkIn(String employeeId);
    AttendanceDto checkIn(String employeeId, Double latitude, Double longitude, String deviceType);

    AttendanceDto checkOut(String employeeId);
    AttendanceDto checkOut(String employeeId, Double latitude, Double longitude, String deviceType);

    AttendanceDto getAttendanceById(Long id);
    List<AttendanceDto> getAllAttendance();
    AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto);
    void deleteAttendance(Long id);

    // ==================== EMPLOYEE-SPECIFIC OPERATIONS ====================
    AttendanceDto getTodayAttendance(String employeeId);
    List<AttendanceDto> getEmployeeAttendanceHistory(String employeeId, LocalDate startDate, LocalDate endDate);
    List<AttendanceDto> getMonthlyAttendance(String employeeId, int year, int month);

    // ==================== ADMIN OPERATIONS ====================
    List<AttendanceDto> getAttendanceByDate(LocalDate date);
    List<AttendanceDto> getTodayAttendanceByStatus(Attendance.AttendanceStatus status);
    AttendanceDto manualCheckIn(String employeeId, LocalTime checkInTime);
    AttendanceDto manualCheckOut(String employeeId, LocalTime checkOutTime);

    // ==================== REPORTS & ANALYTICS ====================
    Integer getTodayPresentCount();
    Integer getTodayAbsentCount();
    Integer getTodayLateCount();

    Map<String, Object> calculateAttendanceSummary(String employeeId, int year, int month);

}