package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.AttendanceDto;
import com.garmentmanagement.garmentmanagement.Entity.Attendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    // Basic CRUD operations
    AttendanceDto checkIn(String employeeId);
    AttendanceDto checkOut(String employeeId);
    AttendanceDto getAttendanceById(Long id);
    List<AttendanceDto> getAllAttendance();
    AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto);
    void deleteAttendance(Long id);

    // Employee-specific operations
    AttendanceDto getTodayAttendance(String employeeId);
    List<AttendanceDto> getEmployeeAttendanceHistory(String employeeId, LocalDate startDate, LocalDate endDate);
    List<AttendanceDto> getMonthlyAttendance(String employeeId, int year, int month);

    // Admin operations
    List<AttendanceDto> getAttendanceByDate(LocalDate date);
    List<AttendanceDto> getTodayAttendanceByStatus(Attendance.AttendanceStatus status);
    AttendanceDto manualCheckIn(String employeeId, LocalTime checkInTime);
    AttendanceDto manualCheckOut(String employeeId, LocalTime checkOutTime);

    // Reports
    Integer getTodayPresentCount();
    Integer getTodayAbsentCount();
    Integer getTodayLateCount();

    Map<String, Object> calculateAttendanceSummary(String employeeId, int year, int month);

}