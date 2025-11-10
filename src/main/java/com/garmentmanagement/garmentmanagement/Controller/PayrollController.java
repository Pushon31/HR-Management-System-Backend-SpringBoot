package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Service.JasperPayslipService;
import com.garmentmanagement.garmentmanagement.Service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final JasperPayslipService jasperPayslipService;

    // ==================== SALARY STRUCTURE ENDPOINTS ====================


    @PostMapping("/salary-structures")
    public ResponseEntity<SalaryStructureDto> createSalaryStructure(@RequestBody SalaryStructureDto salaryStructureDto) {
        SalaryStructureDto created = payrollService.createSalaryStructure(salaryStructureDto);
        return ResponseEntity.ok(created);
    }

    /**
     * ✅ Salary Structure আপডেট করা
     */
    @PutMapping("/salary-structures/{id}")
    public ResponseEntity<SalaryStructureDto> updateSalaryStructure(
            @PathVariable Long id,
            @RequestBody SalaryStructureDto salaryStructureDto) {
        SalaryStructureDto updated = payrollService.updateSalaryStructure(id, salaryStructureDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * ✅ ID দিয়ে Salary Structure পাওয়া
     */
    @GetMapping("/salary-structures/{id}")
    public ResponseEntity<SalaryStructureDto> getSalaryStructure(@PathVariable Long id) {
        SalaryStructureDto salaryStructure = payrollService.getSalaryStructure(id);
        return ResponseEntity.ok(salaryStructure);
    }

    /**
     * ✅ Employee ID দিয়ে Salary Structure পাওয়া
     */
    @GetMapping("/salary-structures/employee/{employeeId}")
    public ResponseEntity<SalaryStructureDto> getSalaryStructureByEmployee(@PathVariable Long employeeId) {
        SalaryStructureDto salaryStructure = payrollService.getSalaryStructureByEmployee(employeeId);
        return ResponseEntity.ok(salaryStructure);
    }

    /**
     * ✅ সব Salary Structures পাওয়া
     */
    @GetMapping("/salary-structures")
    public ResponseEntity<List<SalaryStructureDto>> getAllSalaryStructures() {
        List<SalaryStructureDto> salaryStructures = payrollService.getAllSalaryStructures();
        return ResponseEntity.ok(salaryStructures);
    }

    // ==================== PAYROLL ENDPOINTS ====================

    /**
     * ✅ Single Employee এর জন্য Payroll Process করা
     */
    @PostMapping("/process/{employeeId}")
    public ResponseEntity<PayrollDto> processPayroll(
            @PathVariable Long employeeId,
            @RequestParam String payPeriod) { // Format: "2024-10"
        YearMonth period = YearMonth.parse(payPeriod);
        PayrollDto payroll = payrollService.processPayroll(employeeId, period);
        return ResponseEntity.ok(payroll);
    }

    /**
     * ✅ Bulk Payroll Processing (Multiple Employees)
     */
    @PostMapping("/process/bulk")
    public ResponseEntity<List<PayrollDto>> processBulkPayroll(
            @RequestParam String payPeriod,
            @RequestBody List<Long> employeeIds) {
        YearMonth period = YearMonth.parse(payPeriod);
        List<PayrollDto> payrolls = payrollService.processBulkPayroll(period, employeeIds);
        return ResponseEntity.ok(payrolls);
    }

    /**
     * ✅ Payroll ID দিয়ে Details পাওয়া
     */
    @GetMapping("/{id}")
    public ResponseEntity<PayrollDto> getPayroll(@PathVariable Long id) {
        PayrollDto payroll = payrollService.getPayroll(id);
        return ResponseEntity.ok(payroll);
    }

    /**
     * ✅ Employee এবং Period অনুযায়ী Payroll পাওয়া
     */
    @GetMapping("/employee/{employeeId}/period/{payPeriod}")
    public ResponseEntity<PayrollDto> getEmployeePayrollForPeriod(
            @PathVariable Long employeeId,
            @PathVariable String payPeriod) {
        YearMonth period = YearMonth.parse(payPeriod);
        PayrollDto payroll = payrollService.getEmployeePayrollForPeriod(employeeId, period);
        return ResponseEntity.ok(payroll);
    }

    /**
     * ✅ নির্দিষ্ট Period এর সব Payrolls পাওয়া
     */
    @GetMapping("/period/{payPeriod}")
    public ResponseEntity<List<PayrollDto>> getPayrollsByPeriod(@PathVariable String payPeriod) {
        YearMonth period = YearMonth.parse(payPeriod);
        List<PayrollDto> payrolls = payrollService.getPayrollsByPeriod(period);
        return ResponseEntity.ok(payrolls);
    }

    /**
     * ✅ Employee এর Payroll History পাওয়া
     */
    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<PayrollDto>> getEmployeePayrollHistory(@PathVariable Long employeeId) {
        List<PayrollDto> payrolls = payrollService.getEmployeePayrollHistory(employeeId);
        return ResponseEntity.ok(payrolls);
    }

    /**
     * ✅ Payroll Status আপডেট করা (PENDING → PROCESSED → PAID)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PayrollDto> updatePayrollStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PayrollDto payroll = payrollService.updatePayrollStatus(id, status);
        return ResponseEntity.ok(payroll);
    }

    // ==================== PAYSLIP ENDPOINTS ====================

    /**
     * ✅ Payslip Generate করা
     */
    @PostMapping("/payslips/generate/{payrollId}")
    public ResponseEntity<PayslipDto> generatePayslip(@PathVariable Long payrollId) {
        PayslipDto payslip = payrollService.generatePayslip(payrollId);
        return ResponseEntity.ok(payslip);
    }

    /**
     * ✅ Payslip ID দিয়ে Details পাওয়া
     */
    @GetMapping("/payslips/{id}")
    public ResponseEntity<PayslipDto> getPayslip(@PathVariable Long id) {
        PayslipDto payslip = payrollService.getPayslip(id);
        return ResponseEntity.ok(payslip);
    }

    /**
     * ✅ Payroll ID দিয়ে Payslip পাওয়া
     */
    @GetMapping("/payslips/payroll/{payrollId}")
    public ResponseEntity<PayslipDto> getPayslipByPayroll(@PathVariable Long payrollId) {
        PayslipDto payslip = payrollService.getPayslipByPayroll(payrollId);
        return ResponseEntity.ok(payslip);
    }

    /**
     * ✅ Employee এর সব Payslips পাওয়া
     */
    @GetMapping("/payslips/employee/{employeeId}")
    public ResponseEntity<List<PayslipDto>> getEmployeePayslips(@PathVariable Long employeeId) {
        List<PayslipDto> payslips = payrollService.getEmployeePayslips(employeeId);
        return ResponseEntity.ok(payslips);
    }


    // ✅ Get employee payslips by String employeeId (business ID)
    @GetMapping("/payslips/employee-code/{employeeId}")
    public ResponseEntity<List<PayslipDto>> getEmployeePayslipsByEmployeeId(@PathVariable String employeeId) {
        List<PayslipDto> payslips = payrollService.getEmployeePayslipsByEmployeeId(employeeId);
        return ResponseEntity.ok(payslips);
    }

    // ✅ Get payslips by pay period
    @GetMapping("/payslips/period/{payPeriod}")
    public ResponseEntity<List<PayslipDto>> getPayslipsByPayPeriod(@PathVariable String payPeriod) {
        List<PayslipDto> payslips = payrollService.getPayslipsByPayPeriod(payPeriod);
        return ResponseEntity.ok(payslips);
    }

    // ==================== BONUS ENDPOINTS ====================

    /**
     * ✅ নতুন Bonus তৈরি করা
     */
    @PostMapping("/bonuses")
    public ResponseEntity<BonusDto> createBonus(@RequestBody BonusDto bonusDto) {
        BonusDto bonus = payrollService.createBonus(bonusDto);
        return ResponseEntity.ok(bonus);
    }

    /**
     * ✅ Bonus আপডেট করা
     */
    @PutMapping("/bonuses/{id}")
    public ResponseEntity<BonusDto> updateBonus(
            @PathVariable Long id,
            @RequestBody BonusDto bonusDto) {
        BonusDto bonus = payrollService.updateBonus(id, bonusDto);
        return ResponseEntity.ok(bonus);
    }

    /**
     * ✅ Employee এর সব Bonuses পাওয়া
     */
    @GetMapping("/bonuses/employee/{employeeId}")
    public ResponseEntity<List<BonusDto>> getEmployeeBonuses(@PathVariable Long employeeId) {
        List<BonusDto> bonuses = payrollService.getEmployeeBonuses(employeeId);
        return ResponseEntity.ok(bonuses);
    }

    /**
     * ✅ Date Range অনুযায়ী Bonuses পাওয়া
     */
    @GetMapping("/bonuses/date-range")
    public ResponseEntity<List<BonusDto>> getBonusesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<BonusDto> bonuses = payrollService.getBonusesByDateRange(startDate, endDate);
        return ResponseEntity.ok(bonuses);
    }

    /**
     * ✅ Bonus ডিলিট করা
     */
    @DeleteMapping("/bonuses/{id}")
    public ResponseEntity<Void> deleteBonus(@PathVariable Long id) {
        payrollService.deleteBonus(id);
        return ResponseEntity.ok().build();
    }

    // ==================== REPORTS & DASHBOARD ENDPOINTS ====================

    /**
     * ✅ Payroll Summary পাওয়া (Period অনুযায়ী)
     */
    @GetMapping("/reports/summary/{payPeriod}")
    public ResponseEntity<Map<String, Object>> getPayrollSummary(@PathVariable String payPeriod) {
        YearMonth period = YearMonth.parse(payPeriod);
        Map<String, Object> summary = payrollService.getPayrollSummary(period);
        return ResponseEntity.ok(summary);
    }

    /**
     * ✅ Payroll Dashboard Data পাওয়া
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getPayrollDashboard() {
        Map<String, Object> dashboard = payrollService.getPayrollDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * ✅ Department-wise Payroll Summary
     */
    @GetMapping("/reports/department/{departmentId}/period/{payPeriod}")
    public ResponseEntity<List<PayrollDto>> getDepartmentPayroll(
            @PathVariable Long departmentId,
            @PathVariable String payPeriod) {
        YearMonth period = YearMonth.parse(payPeriod);
        // Note: You might need to add this method in service
        // For now returning empty, implement as needed
        return ResponseEntity.ok(List.of());
    }



    @GetMapping("/payslips/{id}/download")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable Long id) {
        try {
            System.out.println("📥 Download request for payslip ID: " + id);

            // Generate PDF
            byte[] pdfBytes = jasperPayslipService.generatePayslipPdf(id);

            // Create filename
            String filename = "payslip-" + id + ".pdf";

            System.out.println("✅ PDF ready for download: " + filename);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(pdfBytes);

        } catch (Exception e) {
            System.err.println("❌ Error in download endpoint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }
}