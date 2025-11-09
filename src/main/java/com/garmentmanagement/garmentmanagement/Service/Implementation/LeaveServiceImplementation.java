package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.LeaveApplicationDto;
import com.garmentmanagement.garmentmanagement.DTO.LeaveBalanceDto;
import com.garmentmanagement.garmentmanagement.DTO.LeaveTypeDto;
import com.garmentmanagement.garmentmanagement.Entity.*;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Repository.LeaveApplicationRepository;
import com.garmentmanagement.garmentmanagement.Repository.LeaveBalanceRepository;
import com.garmentmanagement.garmentmanagement.Repository.LeaveTypeRepository;
import com.garmentmanagement.garmentmanagement.Service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LeaveServiceImplementation implements LeaveService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    // ==================== LEAVE TYPE METHODS ====================

    @Override
    public LeaveTypeDto createLeaveType(LeaveTypeDto leaveTypeDto) {
        // Check for duplicates
        if (leaveTypeRepository.existsByName(leaveTypeDto.getName())) {
            throw new RuntimeException("Leave type name already exists: " + leaveTypeDto.getName());
        }
        if (leaveTypeRepository.existsByCode(leaveTypeDto.getCode())) {
            throw new RuntimeException("Leave type code already exists: " + leaveTypeDto.getCode());
        }

        LeaveType leaveType = modelMapper.map(leaveTypeDto, LeaveType.class);

        // Set enum category
        if (leaveTypeDto.getCategory() != null) {
            leaveType.setCategory(LeaveType.LeaveCategory.valueOf(leaveTypeDto.getCategory()));
        }

        LeaveType saved = leaveTypeRepository.save(leaveType);
        return convertToLeaveTypeDto(saved);
    }

    @Override
    public LeaveTypeDto updateLeaveType(Long id, LeaveTypeDto leaveTypeDto) {
        LeaveType existing = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found with id: " + id));

        // Check duplicate name
        if (!existing.getName().equals(leaveTypeDto.getName()) &&
                leaveTypeRepository.existsByName(leaveTypeDto.getName())) {
            throw new RuntimeException("Leave type name already exists: " + leaveTypeDto.getName());
        }

        // Check duplicate code
        if (!existing.getCode().equals(leaveTypeDto.getCode()) &&
                leaveTypeRepository.existsByCode(leaveTypeDto.getCode())) {
            throw new RuntimeException("Leave type code already exists: " + leaveTypeDto.getCode());
        }

        modelMapper.map(leaveTypeDto, existing);

        // Update enum
        if (leaveTypeDto.getCategory() != null) {
            existing.setCategory(LeaveType.LeaveCategory.valueOf(leaveTypeDto.getCategory()));
        }

        LeaveType updated = leaveTypeRepository.save(existing);
        return convertToLeaveTypeDto(updated);
    }

    @Override
    public List<LeaveTypeDto> getAllLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .map(this::convertToLeaveTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveTypeDto> getActiveLeaveTypes() {
        return leaveTypeRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToLeaveTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveTypeDto getLeaveTypeById(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found with id: " + id));
        return convertToLeaveTypeDto(leaveType);
    }

    @Override
    public LeaveTypeDto getLeaveTypeByCode(String code) {
        LeaveType leaveType = leaveTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Leave type not found with code: " + code));
        return convertToLeaveTypeDto(leaveType);
    }

    @Override
    public void deleteLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found with id: " + id));

        // Check if leave type is being used
        // Implementation depends on your business rules

        leaveTypeRepository.delete(leaveType);
    }

    // ==================== LEAVE APPLICATION METHODS ====================

    @Override
    public LeaveApplicationDto applyForLeave(LeaveApplicationDto leaveApplicationDto) {
        Employee employee = employeeRepository.findById(leaveApplicationDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + leaveApplicationDto.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(leaveApplicationDto.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found with id: " + leaveApplicationDto.getLeaveTypeId()));

        // Check for overlapping leaves
        List<LeaveApplication> overlappingLeaves = leaveApplicationRepository.findOverlappingLeaves(
                employee.getId(),
                leaveApplicationDto.getStartDate(),
                leaveApplicationDto.getEndDate()
        );

        if (!overlappingLeaves.isEmpty()) {
            throw new RuntimeException("You have overlapping leave applications for the selected dates");
        }

        // Check leave balance
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                        employee.getId(), leaveType.getId(), LocalDate.now().getYear())
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        int requestedDays = calculateWorkingDays(leaveApplicationDto.getStartDate(), leaveApplicationDto.getEndDate());

        if (balance.getRemainingDays() < requestedDays) {
            throw new RuntimeException("Insufficient leave balance. Available: " + balance.getRemainingDays() + ", Requested: " + requestedDays);
        }

        LeaveApplication leaveApplication = modelMapper.map(leaveApplicationDto, LeaveApplication.class);
        leaveApplication.setEmployee(employee);
        leaveApplication.setLeaveType(leaveType);
        leaveApplication.setAppliedDate(LocalDateTime.now());
        leaveApplication.setStatus(LeaveApplication.LeaveStatus.PENDING);
        leaveApplication.setTotalDays(requestedDays);

        LeaveApplication saved = leaveApplicationRepository.save(leaveApplication);
        return convertToLeaveApplicationDto(saved);
    }

    @Override
    public LeaveApplicationDto approveLeave(Long leaveId, Long approvedBy, String remarks) {
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + leaveId));

        Employee approver = employeeRepository.findById(approvedBy)
                .orElseThrow(() -> new RuntimeException("Approver not found with id: " + approvedBy));

        // Update leave balance
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                        leaveApplication.getEmployee().getId(),
                        leaveApplication.getLeaveType().getId(),
                        LocalDate.now().getYear())
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        balance.setUsedDays(balance.getUsedDays() + leaveApplication.getTotalDays());
        balance.calculateRemainingDays();
        leaveBalanceRepository.save(balance);

        // Update leave application
        leaveApplication.setStatus(LeaveApplication.LeaveStatus.APPROVED);
        leaveApplication.setApprovedBy(approver);
        leaveApplication.setRemarks(remarks);
        leaveApplication.setProcessedDate(LocalDateTime.now());

        LeaveApplication updated = leaveApplicationRepository.save(leaveApplication);
        return convertToLeaveApplicationDto(updated);
    }

    @Override
    public LeaveApplicationDto rejectLeave(Long leaveId, Long approvedBy, String remarks) {
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + leaveId));

        Employee approver = employeeRepository.findById(approvedBy)
                .orElseThrow(() -> new RuntimeException("Approver not found with id: " + approvedBy));

        leaveApplication.setStatus(LeaveApplication.LeaveStatus.REJECTED);
        leaveApplication.setApprovedBy(approver);
        leaveApplication.setRemarks(remarks);
        leaveApplication.setProcessedDate(LocalDateTime.now());

        LeaveApplication updated = leaveApplicationRepository.save(leaveApplication);
        return convertToLeaveApplicationDto(updated);
    }

    @Override
    public LeaveApplicationDto cancelLeave(Long leaveId, Long employeeId) {
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + leaveId));

        // Check if employee owns this leave application
        if (!leaveApplication.getEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("You can only cancel your own leave applications");
        }

        // Check if leave is already approved
        if (leaveApplication.getStatus() == LeaveApplication.LeaveStatus.APPROVED) {
            // Return leave balance
            LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                            leaveApplication.getEmployee().getId(),
                            leaveApplication.getLeaveType().getId(),
                            LocalDate.now().getYear())
                    .orElseThrow(() -> new RuntimeException("Leave balance not found"));

            balance.setUsedDays(balance.getUsedDays() - leaveApplication.getTotalDays());
            balance.calculateRemainingDays();
            leaveBalanceRepository.save(balance);
        }

        leaveApplication.setStatus(LeaveApplication.LeaveStatus.CANCELLED);
        LeaveApplication updated = leaveApplicationRepository.save(leaveApplication);
        return convertToLeaveApplicationDto(updated);
    }

    @Override
    public List<LeaveApplicationDto> getLeaveApplicationsByEmployee(Long employeeId) {
        return leaveApplicationRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplicationDto> getPendingLeaveApplications() {

        return leaveApplicationRepository.findPendingLeaves()
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<LeaveApplicationDto> getPendingLeavesForManager(Long managerId) {
        return leaveApplicationRepository.findPendingLeavesForManager(managerId)
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    // ==================== LEAVE BALANCE METHODS ====================

    @Override
    public LeaveBalanceDto getLeaveBalance(Long employeeId, Long leaveTypeId) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                        employeeId, leaveTypeId, LocalDate.now().getYear())
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
        return convertToLeaveBalanceDto(balance);
    }

    @Override
    public List<LeaveBalanceDto> getEmployeeLeaveBalances(Long employeeId) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, LocalDate.now().getYear())
                .stream()
                .map(this::convertToLeaveBalanceDto)
                .collect(Collectors.toList());
    }

    @Override
    public void initializeYearlyLeaveBalances(Integer year) {
        // Implementation for initializing yearly leave balances for all employees
        // This would typically run at the beginning of each year
    }

    // ==================== HELPER METHODS ====================

    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private LeaveTypeDto convertToLeaveTypeDto(LeaveType leaveType) {
        LeaveTypeDto dto = modelMapper.map(leaveType, LeaveTypeDto.class);
        if (leaveType.getCategory() != null) {
            dto.setCategory(leaveType.getCategory().name());
        }
        return dto;
    }

    private LeaveApplicationDto convertToLeaveApplicationDto(LeaveApplication leaveApplication) {
        LeaveApplicationDto dto = modelMapper.map(leaveApplication, LeaveApplicationDto.class);

        // Employee data
        if (leaveApplication.getEmployee() != null) {
            dto.setEmployeeId(leaveApplication.getEmployee().getId());
            dto.setEmployeeName(leaveApplication.getEmployee().getFirstName() + " " + leaveApplication.getEmployee().getLastName());
            dto.setEmployeeCode(leaveApplication.getEmployee().getEmployeeId());

            // Department data
            if (leaveApplication.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(leaveApplication.getEmployee().getDepartment().getName());
            }
        }

        // Leave type data
        if (leaveApplication.getLeaveType() != null) {
            dto.setLeaveTypeId(leaveApplication.getLeaveType().getId());
            dto.setLeaveTypeName(leaveApplication.getLeaveType().getName());
            dto.setLeaveTypeCode(leaveApplication.getLeaveType().getCode());
        }

        // Approver data
        if (leaveApplication.getApprovedBy() != null) {
            dto.setApprovedById(leaveApplication.getApprovedBy().getId());
            dto.setApprovedByName(leaveApplication.getApprovedBy().getFirstName() + " " + leaveApplication.getApprovedBy().getLastName());
        }

        // Status
        if (leaveApplication.getStatus() != null) {
            dto.setStatus(leaveApplication.getStatus().name());
        }

        return dto;
    }

    private LeaveBalanceDto convertToLeaveBalanceDto(LeaveBalance leaveBalance) {
        LeaveBalanceDto dto = modelMapper.map(leaveBalance, LeaveBalanceDto.class);

        if (leaveBalance.getEmployee() != null) {
            dto.setEmployeeId(leaveBalance.getEmployee().getId());
            dto.setEmployeeName(leaveBalance.getEmployee().getFirstName() + " " + leaveBalance.getEmployee().getLastName());
            dto.setEmployeeCode(leaveBalance.getEmployee().getEmployeeId());
        }

        if (leaveBalance.getLeaveType() != null) {
            dto.setLeaveTypeId(leaveBalance.getLeaveType().getId());
            dto.setLeaveTypeName(leaveBalance.getLeaveType().getName());
            dto.setLeaveTypeCode(leaveBalance.getLeaveType().getCode());
        }

        return dto;
    }

    // Add these methods to your LeaveServiceImplementation class:

    @Override
    public LeaveApplicationDto updateLeaveApplication(Long id, LeaveApplicationDto leaveApplicationDto) {
        LeaveApplication existing = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + id));

        // Only allow updates to PENDING leaves
        if (existing.getStatus() != LeaveApplication.LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leaves can be updated");
        }

        modelMapper.map(leaveApplicationDto, existing);

        // Recalculate total days
        existing.calculateTotalDays();

        LeaveApplication updated = leaveApplicationRepository.save(existing);
        return convertToLeaveApplicationDto(updated);
    }

    @Override
    public LeaveApplicationDto getLeaveApplicationById(Long id) {
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + id));
        return convertToLeaveApplicationDto(leaveApplication);
    }

    @Override
    public List<LeaveApplicationDto> getAllLeaveApplications() {
        return leaveApplicationRepository.findAll()
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplicationDto> getLeaveApplicationsByStatus(LeaveApplication.LeaveStatus status) {
        return leaveApplicationRepository.findByStatus(status)
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplicationDto> getLeavesByDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveApplicationRepository.findByStartDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplicationDto> getLeavesByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        // First get all leaves by employee, then filter by date range
        return leaveApplicationRepository.findByEmployeeId(employeeId)
                .stream()
                .filter(leave -> !leave.getStartDate().isAfter(endDate) && !leave.getEndDate().isBefore(startDate))
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplicationDto> getLeavesByDepartment(Long departmentId) {
        return leaveApplicationRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveBalanceDto updateLeaveBalance(Long employeeId, Long leaveTypeId, Integer additionalDays) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                        employeeId, leaveTypeId, LocalDate.now().getYear())
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        balance.setTotalDays(balance.getTotalDays() + additionalDays);
        balance.calculateRemainingDays();

        LeaveBalance updated = leaveBalanceRepository.save(balance);
        return convertToLeaveBalanceDto(updated);
    }

    @Override
    public Map<String, Long> getLeaveStatistics() {
        Map<String, Long> stats = new HashMap<>();

        // Get counts by status
        List<Object[]> statusCounts = leaveApplicationRepository.countLeavesByStatus();
        for (Object[] result : statusCounts) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            stats.put(status, count);
        }

        // Add total count
        stats.put("TOTAL", leaveApplicationRepository.count());
        stats.put("PENDING_COUNT", leaveApplicationRepository.countPendingApplications());

        return stats;
    }

    @Override
    public Map<String, Long> getEmployeeLeaveStatistics(Long employeeId) {
        Map<String, Long> stats = new HashMap<>();

        List<LeaveApplication> employeeLeaves = leaveApplicationRepository.findByEmployeeId(employeeId);

        stats.put("TOTAL", (long) employeeLeaves.size());
        stats.put("PENDING", employeeLeaves.stream()
                .filter(leave -> leave.getStatus() == LeaveApplication.LeaveStatus.PENDING)
                .count());
        stats.put("APPROVED", employeeLeaves.stream()
                .filter(leave -> leave.getStatus() == LeaveApplication.LeaveStatus.APPROVED)
                .count());
        stats.put("REJECTED", employeeLeaves.stream()
                .filter(leave -> leave.getStatus() == LeaveApplication.LeaveStatus.REJECTED)
                .count());

        return stats;
    }

    @Override
    public List<LeaveApplicationDto> getUpcomingLeaves(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);

        return leaveApplicationRepository.findUpcomingLeaves(startDate, endDate)
                .stream()
                .map(this::convertToLeaveApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkLeaveAvailability(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate) {
        // Check for overlapping leaves
        List<LeaveApplication> overlappingLeaves = leaveApplicationRepository.findOverlappingLeaves(
                employeeId, startDate, endDate);

        if (!overlappingLeaves.isEmpty()) {
            return false;
        }

        // Check leave balance
        try {
            LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                            employeeId, leaveTypeId, LocalDate.now().getYear())
                    .orElseThrow(() -> new RuntimeException("Leave balance not found"));

            int requestedDays = calculateWorkingDays(startDate, endDate);
            return balance.getRemainingDays() >= requestedDays;
        } catch (Exception e) {
            return false;
        }
    }


}