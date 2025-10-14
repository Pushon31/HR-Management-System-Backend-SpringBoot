package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface PayrollService {

    // Salary Structure Methods
    SalaryStructureDto createSalaryStructure(SalaryStructureDto salaryStructureDto);
    SalaryStructureDto updateSalaryStructure(Long id, SalaryStructureDto salaryStructureDto);
    SalaryStructureDto getSalaryStructure(Long id);
    SalaryStructureDto getSalaryStructureByEmployee(Long employeeId);
    List<SalaryStructureDto> getAllSalaryStructures();

    // Payroll Methods
    PayrollDto processPayroll(Long employeeId, YearMonth payPeriod);
    PayrollDto getPayroll(Long id);
    PayrollDto getEmployeePayrollForPeriod(Long employeeId, YearMonth payPeriod);
    List<PayrollDto> getPayrollsByPeriod(YearMonth payPeriod);
    List<PayrollDto> getEmployeePayrollHistory(Long employeeId);
    PayrollDto updatePayrollStatus(Long id, String status);

    // Batch Processing
    List<PayrollDto> processBulkPayroll(YearMonth payPeriod, List<Long> employeeIds);
    Map<String, Object> getPayrollSummary(YearMonth payPeriod);

    // Payslip Methods
    PayslipDto generatePayslip(Long payrollId);
    PayslipDto getPayslip(Long id);
    PayslipDto getPayslipByPayroll(Long payrollId);
    List<PayslipDto> getEmployeePayslips(Long employeeId);

    // Bonus Methods
    BonusDto createBonus(BonusDto bonusDto);
    BonusDto updateBonus(Long id, BonusDto bonusDto);
    List<BonusDto> getEmployeeBonuses(Long employeeId);
    List<BonusDto> getBonusesByDateRange(String startDate, String endDate);
    void deleteBonus(Long id);

    // Reports
    Map<String, Object> getPayrollDashboard();
    List<PayslipDto> getEmployeePayslipsByEmployeeId(String employeeId);
    List<PayslipDto> getPayslipsByPayPeriod(String payPeriod);

}