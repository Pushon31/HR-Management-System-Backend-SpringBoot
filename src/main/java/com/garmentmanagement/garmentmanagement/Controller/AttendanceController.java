package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.AttendanceDto;
import com.garmentmanagement.garmentmanagement.Entity.Attendance;
import com.garmentmanagement.garmentmanagement.Service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    //  Check-in
    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<AttendanceDto> checkIn(@PathVariable String employeeId) {
        AttendanceDto attendance = attendanceService.checkIn(employeeId);
        return ResponseEntity.ok(attendance);
    }

    //  Check-out
    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<AttendanceDto> checkOut(@PathVariable String employeeId) {
        AttendanceDto attendance = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(attendance);
    }

    //  Get today's attendance for an employee
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<AttendanceDto> getTodayAttendance(@PathVariable String employeeId) {
        AttendanceDto attendance = attendanceService.getTodayAttendance(employeeId);
        return attendance != null ? ResponseEntity.ok(attendance) : ResponseEntity.noContent().build();
    }

    // Get attendance by ID
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDto> getAttendanceById(@PathVariable Long id) {
        AttendanceDto attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(attendance);
    }

    // Get all attendance records
    @GetMapping
    public ResponseEntity<List<AttendanceDto>> getAllAttendance() {
        List<AttendanceDto> attendanceList = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendanceList);
    }

    // Get employee attendance history
    @GetMapping("/history/{employeeId}")
    public ResponseEntity<List<AttendanceDto>> getEmployeeAttendanceHistory(
            @PathVariable String employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto> attendanceList = attendanceService.getEmployeeAttendanceHistory(employeeId, startDate, endDate);
        return ResponseEntity.ok(attendanceList);
    }

    // Get monthly attendance
    @GetMapping("/monthly/{employeeId}")
    public ResponseEntity<List<AttendanceDto>> getMonthlyAttendance(
            @PathVariable String employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        List<AttendanceDto> attendanceList = attendanceService.getMonthlyAttendance(employeeId, year, month);
        return ResponseEntity.ok(attendanceList);
    }

    // Get attendance by date (Admin)
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDto> attendanceList = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(attendanceList);
    }

    // Get today's attendance by status (Admin)
    @GetMapping("/today/status/{status}")
    public ResponseEntity<List<AttendanceDto>> getTodayAttendanceByStatus(@PathVariable Attendance.AttendanceStatus status) {
        List<AttendanceDto> attendanceList = attendanceService.getTodayAttendanceByStatus(status);
        return ResponseEntity.ok(attendanceList);
    }

    // Manual check-in (Admin)
    @PostMapping("/manual/check-in/{employeeId}")
    public ResponseEntity<AttendanceDto> manualCheckIn(
            @PathVariable String employeeId,
            @RequestParam LocalTime checkInTime) {
        AttendanceDto attendance = attendanceService.manualCheckIn(employeeId, checkInTime);
        return ResponseEntity.ok(attendance);
    }

    // Manual check-out (Admin)
    @PostMapping("/manual/check-out/{employeeId}")
    public ResponseEntity<AttendanceDto> manualCheckOut(
            @PathVariable String employeeId,
            @RequestParam LocalTime checkOutTime) {
        AttendanceDto attendance = attendanceService.manualCheckOut(employeeId, checkOutTime);
        return ResponseEntity.ok(attendance);
    }

    // Update attendance
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDto> updateAttendance(@PathVariable Long id, @RequestBody AttendanceDto attendanceDto) {
        AttendanceDto updatedAttendance = attendanceService.updateAttendance(id, attendanceDto);
        return ResponseEntity.ok(updatedAttendance);
    }

    // Delete attendance
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok().build();
    }

    // Reports - Today's present count
    @GetMapping("/reports/today-present")
    public ResponseEntity<Integer> getTodayPresentCount() {
        Integer count = attendanceService.getTodayPresentCount();
        return ResponseEntity.ok(count);
    }

    // Reports - Today's late count
    @GetMapping("/reports/today-late")
    public ResponseEntity<Integer> getTodayLateCount() {
        Integer count = attendanceService.getTodayLateCount();
        return ResponseEntity.ok(count);
    }


    @GetMapping("/summary/{employeeId}")
    public ResponseEntity<Map<String, Object>> getAttendanceSummary(
            @PathVariable String employeeId,
            @RequestParam int year,
            @RequestParam int month) {

        Map<String, Object> summary = attendanceService.calculateAttendanceSummary(employeeId, year, month);
        return ResponseEntity.ok(summary);
    }

}