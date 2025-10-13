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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
        Employee employee = employeeRepository.findByEmployeeId(employeeId).orElseThrow(()->new RuntimeException("Employee not found"));

        LocalDate today=LocalDate.now();
        LocalTime now=LocalTime.now();

        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(employee.getId(),today)){
            throw new RuntimeException("Attendance already exists");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(today);
        attendance.setCheckinTime(now);
        attendance.setCheckoutTime(now);
        attendance.setStatus(calculateStatus(now));
        attendance.setRemarks("Auto check-in");
        Attendance saved = attendanceRepository.save(attendance);
        return convertToDto(saved);

    }

    @Override
    public AttendanceDto checkOut(String employeeId) {

        Employee employee = employeeRepository.findByEmployeeId(employeeId).orElseThrow(()->new RuntimeException("Employee not found"));
        LocalDate today=LocalDate.now();
        LocalTime now=LocalTime.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employee.getId(),today)
                .orElseThrow(()->new RuntimeException("Check-in not found for today."));


        if (attendance.getCheckoutTime() != null) {
            throw new RuntimeException("Employee already checked-out today.");
        }

        attendance.setAttendanceDate(today);
        attendance.setCheckoutTime(now);
        attendance.setRemarks("Auto check-out");
        Attendance updated = attendanceRepository.save(attendance);
        return convertToDto(updated);

    }

    @Override
    public AttendanceDto getTodayAttendance(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();

        // ✅ Return null if no attendance found
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(
                employee.getId(), today
        ).orElse(null);

        return attendance != null ? convertToDto(attendance) : null;
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
        return attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(), Attendance.AttendanceStatus.PRESENT
        ).size();
    }

    @Override
    public Integer getTodayAbsentCount() {
        // This would need total employees count - present count
        // For now returning 0 as placeholder
        return 0;
    }

    @Override
    public Integer getTodayLateCount() {
        return attendanceRepository.findByAttendanceDateAndStatus(
                LocalDate.now(), Attendance.AttendanceStatus.LATE
        ).size();
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
