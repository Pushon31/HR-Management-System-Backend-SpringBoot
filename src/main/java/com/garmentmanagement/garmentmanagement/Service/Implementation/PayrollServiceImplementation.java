package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Entity.*;
import com.garmentmanagement.garmentmanagement.Repository.*;
import com.garmentmanagement.garmentmanagement.Service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PayrollServiceImplementation implements PayrollService {

    private final SalaryStructureRepository salaryStructureRepo;
    private final PayrollRepository payrollRepo;
    private final PayslipRepository payslipRepo;
    private final BonusRepository bonusRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;
    private final ModelMapper modelMapper;

    // ==================== SALARY STRUCTURE METHODS ====================

    @Override
    public SalaryStructureDto createSalaryStructure(SalaryStructureDto salaryStructureDto) {
        // Check if already exists
        if (salaryStructureRepo.existsByEmployeeId(salaryStructureDto.getEmployeeId())) {
            throw new RuntimeException("Salary structure already exists for this employee");
        }

        Employee employee = employeeRepo.findById(salaryStructureDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        SalaryStructure salaryStructure = modelMapper.map(salaryStructureDto, SalaryStructure.class);
        salaryStructure.setEmployee(employee);
        salaryStructure.calculateSalaries();

        SalaryStructure saved = salaryStructureRepo.save(salaryStructure);
        return convertToSalaryStructureDto(saved);
    }

    @Override
    public SalaryStructureDto updateSalaryStructure(Long id, SalaryStructureDto salaryStructureDto) {
        SalaryStructure existing = salaryStructureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary structure not found"));

        modelMapper.map(salaryStructureDto, existing);
        existing.calculateSalaries();

        SalaryStructure updated = salaryStructureRepo.save(existing);
        return convertToSalaryStructureDto(updated);
    }

    @Override
    public SalaryStructureDto getSalaryStructure(Long id) {
        SalaryStructure salaryStructure = salaryStructureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary structure not found"));
        return convertToSalaryStructureDto(salaryStructure);
    }

    @Override
    public SalaryStructureDto getSalaryStructureByEmployee(Long employeeId) {
        SalaryStructure salaryStructure = salaryStructureRepo.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Salary structure not found for employee"));
        return convertToSalaryStructureDto(salaryStructure);
    }

    @Override
    public List<SalaryStructureDto> getAllSalaryStructures() {
        return salaryStructureRepo.findAll()
                .stream()
                .map(this::convertToSalaryStructureDto)
                .collect(Collectors.toList());
    }

    // ==================== PAYROLL METHODS ====================

    @Override
    public PayrollDto processPayroll(Long employeeId, YearMonth payPeriod) {
        try {
            System.out.println("🔍 Processing payroll for employee: " + employeeId + ", period: " + payPeriod);

            // Check if payroll already processed
            if (payrollRepo.existsByEmployeeIdAndPayPeriod(employeeId, payPeriod)) {
                System.out.println("❌ Payroll already exists for employee " + employeeId + " in period " + payPeriod);
                throw new RuntimeException("PAYROLL_ALREADY_EXISTS: Payroll already processed for employee " + employeeId + " in period " + payPeriod);
            }

            Employee employee = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            SalaryStructure salaryStructure = salaryStructureRepo.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("Salary structure not found for employee: " + employeeId));

            // Get attendance data for the period
            int year = payPeriod.getYear();
            int month = payPeriod.getMonthValue();
            List<Attendance> attendanceList = attendanceRepo.findByEmployeeIdAndMonth(employeeId, year, month);

            long presentDays = attendanceList.stream()
                    .filter(att -> att.getStatus() == Attendance.AttendanceStatus.PRESENT ||
                            att.getStatus() == Attendance.AttendanceStatus.LATE)
                    .count();

            long workingDays = LocalDate.of(year, month, 1).lengthOfMonth();

            // Calculate payroll
            Payroll payroll = new Payroll();
            payroll.setEmployee(employee);
            payroll.setPayPeriod(payPeriod);
            payroll.setPayDate(LocalDate.now());
            payroll.setBasicSalary(salaryStructure.getBasicSalary());
            payroll.setTotalAllowances(salaryStructure.getHouseRent()
                    .add(salaryStructure.getMedicalAllowance())
                    .add(salaryStructure.getTransportAllowance())
                    .add(salaryStructure.getOtherAllowances()));
            payroll.setWorkingDays((int) workingDays);
            payroll.setPresentDays((int) presentDays);

            // Calculate deductions for absent days
            double dailySalary = salaryStructure.getBasicSalary().doubleValue() / workingDays;
            long absentDays = workingDays - presentDays;
            payroll.setOtherDeductions(BigDecimal.valueOf(absentDays * dailySalary));

            payroll.calculatePayroll();
            payroll.setStatus(Payroll.PayrollStatus.PROCESSED);

            Payroll saved = payrollRepo.save(payroll);
            System.out.println("✅ Payroll processed successfully: " + saved.getId());

            // ✅ AUTO-GENERATE PAYSLIP AFTER PAYROLL PROCESSING
            try {
                System.out.println("🔄 Auto-generating payslip for payroll ID: " + saved.getId());
                PayslipDto payslip = generatePayslip(saved.getId());
                System.out.println("✅ Payslip auto-generated successfully: " + payslip.getPayslipCode());
            } catch (Exception payslipException) {
                System.err.println("⚠️ Payroll processed but payslip generation failed: " + payslipException.getMessage());
                // Don't throw - payroll is still processed successfully
                // Log the error but don't stop the payroll process
            }

            return convertToPayrollDto(saved);

        } catch (RuntimeException e) {
            // Re-throw business exceptions (like duplicate payroll)
            if (e.getMessage().contains("PAYROLL_ALREADY_EXISTS")) {
                System.err.println("🔴 Business exception: " + e.getMessage());
                throw e;
            }
            System.err.println("❌ Error processing payroll: " + e.getMessage());
            throw new RuntimeException("Failed to process payroll: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error processing payroll: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error processing payroll: " + e.getMessage(), e);
        }
    }
    @Override
    public List<PayrollDto> processBulkPayroll(YearMonth payPeriod, List<Long> employeeIds) {
        return employeeIds.stream()
                .map(employeeId -> processPayroll(employeeId, payPeriod))
                .collect(Collectors.toList());
    }

    @Override
    public PayrollDto getPayroll(Long id) {
        Payroll payroll = payrollRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
        return convertToPayrollDto(payroll);
    }

    @Override
    public PayrollDto getEmployeePayrollForPeriod(Long employeeId, YearMonth payPeriod) {
        Payroll payroll = payrollRepo.findByEmployeeIdAndPayPeriod(employeeId, payPeriod)
                .orElseThrow(() -> new RuntimeException("Payroll not found for period"));
        return convertToPayrollDto(payroll);
    }

    @Override
    public List<PayrollDto> getPayrollsByPeriod(YearMonth payPeriod) {
        return payrollRepo.findByPayPeriod(payPeriod)
                .stream()
                .map(this::convertToPayrollDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayrollDto> getEmployeePayrollHistory(Long employeeId) {
        return payrollRepo.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToPayrollDto)
                .collect(Collectors.toList());
    }

    @Override
    public PayrollDto updatePayrollStatus(Long id, String status) {
        Payroll payroll = payrollRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        payroll.setStatus(Payroll.PayrollStatus.valueOf(status));
        Payroll updated = payrollRepo.save(payroll);
        return convertToPayrollDto(updated);
    }

    // ==================== PAYSLIP METHODS ====================

    @Override
    public PayslipDto generatePayslip(Long payrollId) {
        Payroll payroll = payrollRepo.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        // Check if payslip already exists
        if (payslipRepo.findByPayrollId(payrollId).isPresent()) {
            throw new RuntimeException("Payslip already generated for this payroll");
        }

        Payslip payslip = new Payslip();
        payslip.setPayroll(payroll);
        payslip.setPayslipCode("PS-" + payroll.getPayPeriod() + "-" + payroll.getEmployee().getEmployeeId());
        payslip.setIssueDate(LocalDate.now());
        payslip.setIsGenerated(true);

        Payslip saved = payslipRepo.save(payslip);
        return convertToPayslipDto(saved);
    }

    @Override
    public PayslipDto getPayslip(Long id) {
        Payslip payslip = payslipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found"));
        return convertToPayslipDto(payslip);
    }

    @Override
    public PayslipDto getPayslipByPayroll(Long payrollId) {
        Payslip payslip = payslipRepo.findByPayrollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Payslip not found for this payroll"));
        return convertToPayslipDto(payslip);
    }

    @Override
    public List<PayslipDto> getEmployeePayslips(Long employeeId) {
        // ✅ FIXED: This will now work with the custom query
        return payslipRepo.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToPayslipDto)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Get payslips by employee business ID (String)
    public List<PayslipDto> getEmployeePayslipsByEmployeeId(String employeeId) {
        return payslipRepo.findByEmployeeEmployeeId(employeeId)
                .stream()
                .map(this::convertToPayslipDto)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Get payslips by pay period
    public List<PayslipDto> getPayslipsByPayPeriod(String payPeriod) {
        try {
            System.out.println("🔍 Processing payslips for period: " + payPeriod);

            // Convert String to YearMonth
            YearMonth period = YearMonth.parse(payPeriod);
            System.out.println("✅ Parsed to YearMonth: " + period);

            List<Payslip> payslips = payslipRepo.findByPayPeriod(period);
            System.out.println("✅ Found " + payslips.size() + " payslips");

            return payslips.stream()
                    .map(this::convertToPayslipDto)
                    .collect(Collectors.toList());

        } catch (DateTimeParseException e) {
            System.err.println("❌ Error parsing pay period: " + payPeriod);
            throw new IllegalArgumentException("Invalid pay period format. Expected: yyyy-MM", e);
        }
    }
    // ==================== BONUS METHODS ====================

    @Override
    public BonusDto createBonus(BonusDto bonusDto) {
        Employee employee = employeeRepo.findById(bonusDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Bonus bonus = modelMapper.map(bonusDto, Bonus.class);
        bonus.setEmployee(employee);

        Bonus saved = bonusRepo.save(bonus);
        return convertToBonusDto(saved);
    }

    @Override
    public BonusDto updateBonus(Long id, BonusDto bonusDto) {
        Bonus existing = bonusRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus not found"));

        modelMapper.map(bonusDto, existing);
        Bonus updated = bonusRepo.save(existing);
        return convertToBonusDto(updated);
    }

    @Override
    public List<BonusDto> getEmployeeBonuses(Long employeeId) {
        return bonusRepo.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToBonusDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BonusDto> getBonusesByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return bonusRepo.findByBonusDateBetween(start, end)
                .stream()
                .map(this::convertToBonusDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBonus(Long id) {
        Bonus bonus = bonusRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus not found"));
        bonusRepo.delete(bonus);
    }

    // ==================== REPORTS & DASHBOARD ====================

    @Override
    public Map<String, Object> getPayrollSummary(YearMonth payPeriod) {
        Map<String, Object> summary = new HashMap<>();

        Long totalEmployees = payrollRepo.countByPayPeriod(payPeriod);
        Double totalExpense = payrollRepo.getTotalSalaryExpenseByPeriod(payPeriod);
        Long pendingCount = (long) payrollRepo.findPendingPayrolls().size();

        summary.put("totalEmployees", totalEmployees);
        summary.put("totalExpense", totalExpense);
        summary.put("pendingCount", pendingCount);
        summary.put("payPeriod", payPeriod.toString());

        return summary;
    }

    @Override
    public Map<String, Object> getPayrollDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        Double avgSalary = salaryStructureRepo.findAverageSalary();
        Long generatedPayslips = payslipRepo.countGeneratedPayslips();
        Long pendingPayrolls = (long) payrollRepo.findPendingPayrolls().size();

        dashboard.put("averageSalary", avgSalary);
        dashboard.put("generatedPayslips", generatedPayslips);
        dashboard.put("pendingPayrolls", pendingPayrolls);
        dashboard.put("totalEmployees", employeeRepo.count());

        return dashboard;
    }

    // ==================== DTO CONVERSION METHODS ====================

    private SalaryStructureDto convertToSalaryStructureDto(SalaryStructure salaryStructure) {
        SalaryStructureDto dto = modelMapper.map(salaryStructure, SalaryStructureDto.class);

        if (salaryStructure.getEmployee() != null) {
            dto.setEmployeeId(salaryStructure.getEmployee().getId());
            dto.setEmployeeName(salaryStructure.getEmployee().getFirstName() + " " + salaryStructure.getEmployee().getLastName());
            dto.setEmployeeCode(salaryStructure.getEmployee().getEmployeeId());
            dto.setDesignation(salaryStructure.getEmployee().getDesignation());

            if (salaryStructure.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(salaryStructure.getEmployee().getDepartment().getName());
            }
        }

        if (salaryStructure.getStatus() != null) {
            dto.setStatus(salaryStructure.getStatus().name());
        }

        return dto;
    }

    private PayrollDto convertToPayrollDto(Payroll payroll) {
        PayrollDto dto = modelMapper.map(payroll, PayrollDto.class);

        if (payroll.getEmployee() != null) {
            dto.setEmployeeId(payroll.getEmployee().getId());
            dto.setEmployeeName(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName());
            dto.setEmployeeCode(payroll.getEmployee().getEmployeeId());
            dto.setDesignation(payroll.getEmployee().getDesignation());

            if (payroll.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(payroll.getEmployee().getDepartment().getName());
            }
        }

        if (payroll.getStatus() != null) {
            dto.setStatus(payroll.getStatus().name());
        }

        return dto;
    }

    private PayslipDto convertToPayslipDto(Payslip payslip) {
        PayslipDto dto = modelMapper.map(payslip, PayslipDto.class);

        if (payslip.getPayroll() != null) {
            Payroll payroll = payslip.getPayroll();
            dto.setPayrollId(payroll.getId());
            dto.setPayPeriod(payroll.getPayPeriod().toString());
            dto.setBasicSalary(payroll.getBasicSalary().doubleValue());
            dto.setTotalAllowances(payroll.getTotalAllowances().doubleValue());
            dto.setDeductions(payroll.getOtherDeductions().doubleValue());
            dto.setNetSalary(payroll.getNetSalary().doubleValue());

            if (payroll.getEmployee() != null) {
                dto.setEmployeeId(payroll.getEmployee().getId());
                dto.setEmployeeName(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName());
                dto.setEmployeeCode(payroll.getEmployee().getEmployeeId());
                dto.setDesignation(payroll.getEmployee().getDesignation());

                if (payroll.getEmployee().getDepartment() != null) {
                    dto.setDepartmentName(payroll.getEmployee().getDepartment().getName());
                }
            }
        }

        if (payslip.getStatus() != null) {
            dto.setStatus(payslip.getStatus().name());
        }

        return dto;
    }

    private BonusDto convertToBonusDto(Bonus bonus) {
        BonusDto dto = modelMapper.map(bonus, BonusDto.class);

        if (bonus.getEmployee() != null) {
            dto.setEmployeeId(bonus.getEmployee().getId());
            dto.setEmployeeName(bonus.getEmployee().getFirstName() + " " + bonus.getEmployee().getLastName());
            dto.setEmployeeCode(bonus.getEmployee().getEmployeeId());
            dto.setDesignation(bonus.getEmployee().getDesignation());

            if (bonus.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(bonus.getEmployee().getDepartment().getName());
            }
        }

        if (bonus.getStatus() != null) {
            dto.setStatus(bonus.getStatus().name());
        }

        return dto;
    }
}