package com.garmentmanagement.garmentmanagement.Service.Implementation;


import com.garmentmanagement.garmentmanagement.DTO.AttendanceDto;
import com.garmentmanagement.garmentmanagement.Entity.Attendance;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Repository.AttendanceRepository;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceServiceImplementation implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    private final LocalTime SHIFT_START = LocalTime.of(9, 0); //9 am
    private final LocalTime SHIFT_END = LocalTime.of(18, 0);
    private final int GRACE_PERIOD = 15;



    @Override
    public AttendanceDto checkIn(String employeeId) {
        // 1. Find employee by business ID (employeeId)
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 2. Check if already checked in today
        LocalDate today = LocalDate.now();
        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(employee.getId(), today)) {
            throw new RuntimeException("Already checked in for today");
        }

        // 3. Create new attendance
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(today);
        attendance.setCheckinTime(LocalTime.now());

        // 4. Calculate status based on check-in time
        LocalTime currentTime = LocalTime.now();
        LocalTime lateThreshold = LocalTime.of(9, 15); // 9:15 AM
        LocalTime absentThreshold = LocalTime.of(10, 0); // 10:00 AM

        if (currentTime.isBefore(lateThreshold)) {
            attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        } else if (currentTime.isBefore(absentThreshold)) {
            attendance.setStatus(Attendance.AttendanceStatus.LATE);
        } else {
            attendance.setStatus(Attendance.AttendanceStatus.ABSENT);
        }

        // 5. Set initial values
        attendance.setRemarks("Auto check-in");
        attendance.setTotalHours(0.0);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return convertToDto(savedAttendance);
    }

    @Override
    public AttendanceDto checkOut(String employeeId) {
        // 1. Find today's attendance
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employee.getId(), today)
                .orElseThrow(() -> new RuntimeException("No check-in found for today"));

        // 2. Check if already checked out
        if (attendance.getCheckoutTime() != null) {
            throw new RuntimeException("Already checked out for today");
        }

        // 3. Set check-out time
        attendance.setCheckoutTime(LocalTime.now());

        // 4. Calculate total working hours
        if (attendance.getCheckinTime() != null && attendance.getCheckoutTime() != null) {
            Duration duration = Duration.between(attendance.getCheckinTime(), attendance.getCheckoutTime());
            double hours = duration.toMinutes() / 60.0;
            attendance.setTotalHours(hours);

            // 5. Update status for half-day
            if (hours < 4.0) {
                attendance.setStatus(Attendance.AttendanceStatus.HALF_DAY);
            }
        }

        // 6. Update remarks
        attendance.setRemarks(attendance.getRemarks() + " | Auto check-out");

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return convertToDto(savedAttendance);
    }

    @Override
    public AttendanceDto getTodayAttendance(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();
        Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeIdAndAttendanceDate(employee.getId(), today);

        if (attendanceOpt.isPresent()) {
            return convertToDto(attendanceOpt.get());
        } else {
            // Auto-create ABSENT record if no check-in by 10 AM
            LocalTime currentTime = LocalTime.now();
            if (currentTime.isAfter(LocalTime.of(10, 0))) { // After 10 AM
                Attendance absentAttendance = new Attendance();
                absentAttendance.setEmployee(employee);
                absentAttendance.setAttendanceDate(today);
                absentAttendance.setStatus(Attendance.AttendanceStatus.ABSENT);
                absentAttendance.setRemarks("Auto-marked absent");
                absentAttendance.setTotalHours(0.0);

                Attendance saved = attendanceRepository.save(absentAttendance);
                return convertToDto(saved);
            }

            return null; // No attendance record yet
        }
    }

    @Override
    public Map<String, Object> calculateAttendanceSummary(String employeeId, int year, int month) {
        // Get monthly attendance for the employee
        List<AttendanceDto> monthlyAttendance = getMonthlyAttendance(employeeId, year, month);

        // Calculate counts for each status
        long totalPresent = monthlyAttendance.stream()
                .filter(a -> a.getStatus().equals("PRESENT") || a.getStatus().equals("PRESENT"))
                .count();

        long totalLate = monthlyAttendance.stream()
                .filter(a -> a.getStatus().equals("LATE") || a.getStatus().equals("LATE"))
                .count();

        long totalAbsent = monthlyAttendance.stream()
                .filter(a -> a.getStatus().equals("ABSENT") || a.getStatus().equals("ABSENT"))
                .count();

        long totalHalfDay = monthlyAttendance.stream()
                .filter(a -> a.getStatus().equals("HALF_DAY") || a.getStatus().equals("HALF_DAY"))
                .count();

        // Calculate total working days in the month
        int totalWorkingDays = getWorkingDaysInMonth(year, month);

        // Calculate attendance percentage
        double attendancePercentage = 0.0;
        if (totalWorkingDays > 0) {
            attendancePercentage = (double) (totalPresent + totalLate) / totalWorkingDays * 100;
        }

        // Create summary map
        Map<String, Object> summary = new HashMap<>();
        summary.put("employeeId", employeeId);
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalPresent", totalPresent);
        summary.put("totalLate", totalLate);
        summary.put("totalAbsent", totalAbsent);
        summary.put("totalHalfDay", totalHalfDay);
        summary.put("totalWorkingDays", totalWorkingDays);
        summary.put("attendancePercentage", Math.round(attendancePercentage * 100.0) / 100.0);

        return summary;
    }

    // ✅ PRIVATE HELPER METHOD - Only in ServiceImpl
    private int getWorkingDaysInMonth(int year, int month) {
        // Simple implementation - exclude weekends
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        int workingDays = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        return workingDays;
    }


    @Override
    public List<AttendanceDto> getEmployeeAttendanceHistory(String employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employee.getId(), startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> getMonthlyAttendance(String employeeId, int year, int month) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return attendanceRepository.findByEmployeeIdAndMonth(employee.getId(), year, month)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> getTodayAttendanceByStatus(Attendance.AttendanceStatus status) {
        return attendanceRepository.findByAttendanceDateAndStatus(LocalDate.now(), status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDto manualCheckIn(String employeeId, LocalTime checkInTime) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        LocalDate today = LocalDate.now();

        // Check if already checked in today
        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(employee.getId(), today)) {
            throw new RuntimeException("Employee already checked in today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(today);
        attendance.setCheckinTime(checkInTime);
        attendance.setStatus(calculateStatus(checkInTime));
        attendance.setRemarks("Manual check-in by admin");

        Attendance saved = attendanceRepository.save(attendance);
        return convertToDto(saved);
    }

    @Override
    public AttendanceDto manualCheckOut(String employeeId, LocalTime checkOutTime) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employee.getId(), today)
                .orElseThrow(() -> new RuntimeException("No check-in found for today"));

        attendance.setCheckoutTime(checkOutTime);
        attendance.setTotalHours(calculateTotalHours(attendance.getCheckinTime(), checkOutTime));
        attendance.setRemarks("Manual check-out by admin");

        Attendance updated = attendanceRepository.save(attendance);
        return convertToDto(updated);
    }

    @Override
    public AttendanceDto getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));
        return convertToDto(attendance);
    }

    @Override
    public List<AttendanceDto> getAllAttendance() {
        return attendanceRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));

        // Use ModelMapper for basic fields
        modelMapper.map(attendanceDto, existing);

        // Manual handle for enum and relationships
        if (attendanceDto.getStatus() != null) {
            existing.setStatus(Attendance.AttendanceStatus.valueOf(attendanceDto.getStatus()));
        }

        Attendance updated = attendanceRepository.save(existing);
        return convertToDto(updated);
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));
        attendanceRepository.delete(attendance);
    }

    @Override
    public Integer getTodayPresentCount() {
        List<Attendance> todayPresent = attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(),
                Attendance.AttendanceStatus.PRESENT
        );
        return todayPresent.size();
    }

    @Override
    public Integer getTodayLateCount() {
        List<Attendance> todayLate = attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(),
                Attendance.AttendanceStatus.LATE
        );
        return todayLate.size();
    }

    @Override
    public Integer getTodayAbsentCount() {
        List<Attendance> todayAbsent = attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(),
                Attendance.AttendanceStatus.ABSENT
        );
        return todayAbsent.size();
    }

    // ==================== HELPER METHODS ====================

    private Attendance.AttendanceStatus calculateStatus(LocalTime checkInTime) {
        if (checkInTime == null) {
            return Attendance.AttendanceStatus.ABSENT;
        }

        if (checkInTime.isBefore(SHIFT_START.plusMinutes(GRACE_PERIOD))) {
            return Attendance.AttendanceStatus.PRESENT;
        } else if (checkInTime.isBefore(SHIFT_START.plusHours(2))) {
            return Attendance.AttendanceStatus.LATE;
        } else {
            return Attendance.AttendanceStatus.HALF_DAY;
        }
    }

    private Double calculateTotalHours(LocalTime checkIn, LocalTime checkOut) {
        if (checkIn == null || checkOut == null) return 0.0;
        long minutes = java.time.Duration.between(checkIn, checkOut).toMinutes();
        return minutes / 60.0;
    }

    private AttendanceDto convertToDto(Attendance attendance) {
        // ❌ Remove ModelMapper - manually map everything
        AttendanceDto dto = new AttendanceDto();

        // ✅ Manual mapping for ALL fields
        dto.setId(attendance.getId());
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setCheckinTime(attendance.getCheckinTime());  // ✅ This was missing!
        dto.setCheckoutTime(attendance.getCheckoutTime()); // ✅ This was missing!
        dto.setTotalHours(attendance.getTotalHours());
        dto.setRemarks(attendance.getRemarks());

        // Status enum to string
        if (attendance.getStatus() != null) {
            dto.setStatus(attendance.getStatus().name());
        }

        // Employee info
        if (attendance.getEmployee() != null) {
            dto.setEmployeeId(attendance.getEmployee().getEmployeeId());
            dto.setEmployeeName(
                    attendance.getEmployee().getFirstName() + " " +
                            attendance.getEmployee().getLastName()
            );

            if (attendance.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(attendance.getEmployee().getDepartment().getName());
            }
        }

        return dto;
    }


}
