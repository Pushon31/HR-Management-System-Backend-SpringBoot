package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.Config.CompanyConfig;
import com.garmentmanagement.garmentmanagement.Entity.*;
import com.garmentmanagement.garmentmanagement.Repository.*;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JasperPayslipService {

    private final CompanyConfig companyConfig;
    private final PayslipRepository payslipRepository;
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final DepartmentRepository departmentRepository;

    public byte[] generatePayslipPdf(Long payslipId) {
        try {
            System.out.println("🔄 Generating payslip PDF for ID: " + payslipId);

            // 1. Load JRXML template from resources
            InputStream templateStream = new ClassPathResource("reports/payslip_template.jrxml").getInputStream();

            // 2. Compile the report
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // 3. Prepare parameters with actual data
            Map<String, Object> parameters = prepareParameters(payslipId);

            // 4. Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // 5. Export to PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            System.out.println("✅ Payslip PDF generated successfully: " + pdfBytes.length + " bytes");
            return pdfBytes;

        } catch (Exception e) {
            System.err.println("❌ Error generating payslip PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Payslip PDF generation failed for ID: " + payslipId, e);
        }
    }

    private Map<String, Object> prepareParameters(Long payslipId) {
        Map<String, Object> parameters = new HashMap<>();

        try {
            // Fetch data from database
            Payslip payslip = payslipRepository.findById(payslipId)
                    .orElseThrow(() -> new RuntimeException("Payslip not found with ID: " + payslipId));

            Payroll payroll = payslip.getPayroll();
            Employee employee = payroll.getEmployee();
            Department department = employee.getDepartment();
            SalaryStructure salaryStructure = salaryStructureRepository.findByEmployee(employee)
                    .orElse(new SalaryStructure());

            // Add company parameters
            parameters.putAll(companyConfig.getReportParams());

            // Add payslip ID parameter
            parameters.put("payslip_id", payslipId);

            // Employee Information
            parameters.put("EMPLOYEE_NAME", getStringValue(employee.getFirstName() + " " + employee.getLastName()));
            parameters.put("EMPLOYEE_ID", getStringValue(employee.getEmployeeId()));
            parameters.put("DESIGNATION", getStringValue(employee.getDesignation()));
            parameters.put("DEPARTMENT", getStringValue(department != null ? department.getName() : "N/A"));
            parameters.put("EMAIL", getStringValue(employee.getEmail()));
            parameters.put("PHONE_NUMBER", getStringValue(employee.getPhoneNumber()));

            // Payroll Information - Convert all to String or proper types
            parameters.put("PAY_PERIOD", convertPayPeriodToString(payroll.getPayPeriod()));
            parameters.put("ISSUE_DATE", convertToSqlDate(payslip.getIssueDate()));
            parameters.put("PAY_DATE", convertToSqlDate(payroll.getPayDate()));
            parameters.put("PAYSLIP_CODE", getStringValue(payslip.getPayslipCode()));

            // Attendance
            parameters.put("WORKING_DAYS", payroll.getWorkingDays() != null ? payroll.getWorkingDays() : 0);
            parameters.put("PRESENT_DAYS", payroll.getPresentDays() != null ? payroll.getPresentDays() : 0);
            int absentDays = (payroll.getWorkingDays() != null && payroll.getPresentDays() != null)
                    ? payroll.getWorkingDays() - payroll.getPresentDays() : 0;
            parameters.put("ABSENT_DAYS", absentDays);

            // Salary Components
            BigDecimal basicSalary = getBigDecimalValue(salaryStructure.getBasicSalary(), payroll.getBasicSalary());
            BigDecimal houseRent = getBigDecimalValue(salaryStructure.getHouseRent());
            BigDecimal medicalAllowance = getBigDecimalValue(salaryStructure.getMedicalAllowance());
            BigDecimal transportAllowance = getBigDecimalValue(salaryStructure.getTransportAllowance());
            BigDecimal otherAllowances = getBigDecimalValue(salaryStructure.getOtherAllowances());

            parameters.put("BASIC_SALARY", basicSalary);
            parameters.put("HOUSE_RENT", houseRent);
            parameters.put("MEDICAL_ALLOWANCE", medicalAllowance);
            parameters.put("TRANSPORT_ALLOWANCE", transportAllowance);
            parameters.put("OTHER_ALLOWANCES", otherAllowances);

            // Calculate totals
            BigDecimal totalEarnings = basicSalary
                    .add(houseRent)
                    .add(medicalAllowance)
                    .add(transportAllowance)
                    .add(otherAllowances);

            parameters.put("TOTAL_EARNINGS", totalEarnings);

            // Deductions
            BigDecimal taxDeduction = getBigDecimalValue(payroll.getTaxDeduction());
            BigDecimal otherDeductions = getBigDecimalValue(payroll.getOtherDeductions());

            // Calculate absent days deduction
            BigDecimal dailyRate = basicSalary.divide(
                    new BigDecimal(payroll.getWorkingDays() != null && payroll.getWorkingDays() > 0 ?
                            payroll.getWorkingDays() : 1),
                    2, BigDecimal.ROUND_HALF_UP
            );
            BigDecimal absentDeduction = dailyRate.multiply(new BigDecimal(absentDays));

            parameters.put("TAX_DEDUCTION", taxDeduction);
            parameters.put("OTHER_DEDUCTIONS", otherDeductions);
            parameters.put("ABSENT_DAYS_DEDUCTION", absentDeduction);

            BigDecimal totalDeductions = taxDeduction.add(otherDeductions).add(absentDeduction);
            parameters.put("TOTAL_DEDUCTIONS", totalDeductions);

            // Net Salary
            BigDecimal netSalary = getBigDecimalValue(payroll.getNetSalary(), totalEarnings.subtract(totalDeductions));
            parameters.put("NET_SALARY", netSalary);

            // Additional fields
            parameters.put("OVERTIME_PAY", getBigDecimalValue(payroll.getOvertimePay()));
            parameters.put("BONUS", getBigDecimalValue(payroll.getBonus()));
            parameters.put("GROSS_SALARY", getBigDecimalValue(payroll.getGrossSalary(), totalEarnings));

            System.out.println("📋 Prepared parameters for employee: " + parameters.get("EMPLOYEE_NAME"));
            System.out.println("💰 Net Salary: " + parameters.get("NET_SALARY"));
            System.out.println("📅 Pay Period: " + parameters.get("PAY_PERIOD"));

        } catch (Exception e) {
            System.err.println("❌ Error preparing parameters: " + e.getMessage());
            e.printStackTrace();
            setDefaultParameters(parameters, payslipId);
        }

        return parameters;
    }

    // Helper methods for type conversion
    private String getStringValue(Object value) {
        return value != null ? value.toString() : "N/A";
    }

    private String getStringValue(String value) {
        return value != null ? value : "N/A";
    }

    private BigDecimal getBigDecimalValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal getBigDecimalValue(BigDecimal primary, BigDecimal fallback) {
        return primary != null ? primary : (fallback != null ? fallback : BigDecimal.ZERO);
    }

    private Date convertToSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    private Date convertToSqlDate(java.util.Date utilDate) {
        return utilDate != null ? new Date(utilDate.getTime()) : null;
    }

    private String convertPayPeriodToString(Object payPeriod) {
        if (payPeriod == null) {
            return "N/A";
        }

        if (payPeriod instanceof YearMonth) {
            YearMonth yearMonth = (YearMonth) payPeriod;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return yearMonth.format(formatter);
        } else if (payPeriod instanceof String) {
            return (String) payPeriod;
        } else {
            return payPeriod.toString();
        }
    }

    private void setDefaultParameters(Map<String, Object> parameters, Long payslipId) {
        // Set default values if database fetch fails
        parameters.putAll(companyConfig.getReportParams());
        parameters.put("payslip_id", payslipId);
        parameters.put("EMPLOYEE_NAME", "Employee Name");
        parameters.put("EMPLOYEE_ID", "EMP001");
        parameters.put("DESIGNATION", "Designation");
        parameters.put("DEPARTMENT", "Department");
        parameters.put("EMAIL", "email@example.com");
        parameters.put("PHONE_NUMBER", "0123456789");
        parameters.put("PAY_PERIOD", "January 2024");
        parameters.put("PAYSLIP_CODE", "PSL" + payslipId);
        parameters.put("WORKING_DAYS", 26);
        parameters.put("PRESENT_DAYS", 24);
        parameters.put("ABSENT_DAYS", 2);
        parameters.put("BASIC_SALARY", new BigDecimal("30000.00"));
        parameters.put("HOUSE_RENT", new BigDecimal("15000.00"));
        parameters.put("MEDICAL_ALLOWANCE", new BigDecimal("2000.00"));
        parameters.put("TRANSPORT_ALLOWANCE", new BigDecimal("1000.00"));
        parameters.put("OTHER_ALLOWANCES", new BigDecimal("500.00"));
        parameters.put("TOTAL_EARNINGS", new BigDecimal("48500.00"));
        parameters.put("TAX_DEDUCTION", new BigDecimal("2000.00"));
        parameters.put("OTHER_DEDUCTIONS", new BigDecimal("1000.00"));
        parameters.put("ABSENT_DAYS_DEDUCTION", new BigDecimal("2307.69"));
        parameters.put("TOTAL_DEDUCTIONS", new BigDecimal("5307.69"));
        parameters.put("NET_SALARY", new BigDecimal("43192.31"));
        parameters.put("OVERTIME_PAY", BigDecimal.ZERO);
        parameters.put("BONUS", BigDecimal.ZERO);
        parameters.put("GROSS_SALARY", new BigDecimal("48500.00"));

        // Set current dates
        parameters.put("ISSUE_DATE", new Date(System.currentTimeMillis()));
        parameters.put("PAY_DATE", new Date(System.currentTimeMillis()));
    }
}