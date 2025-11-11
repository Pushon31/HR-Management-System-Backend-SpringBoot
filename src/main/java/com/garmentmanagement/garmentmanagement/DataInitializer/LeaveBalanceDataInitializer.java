package com.garmentmanagement.garmentmanagement.DataInitializer;

import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Entity.LeaveBalance;
import com.garmentmanagement.garmentmanagement.Entity.LeaveType;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Repository.LeaveBalanceRepository;
import com.garmentmanagement.garmentmanagement.Repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(2) // Run after LeaveTypeDataInitializer
public class LeaveBalanceDataInitializer implements ApplicationRunner {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeLeaveBalancesForCurrentYear();
    }

    private void initializeLeaveBalancesForCurrentYear() {
        List<Employee> employees = employeeRepository.findAll();
        List<LeaveType> activeLeaveTypes = leaveTypeRepository.findByIsActiveTrue();
        int currentYear = java.time.Year.now().getValue();

        if (employees.isEmpty() || activeLeaveTypes.isEmpty()) {
            System.out.println(" No employees or leave types found for balance initialization");
            return;
        }

        int createdCount = 0;
        int updatedCount = 0;

        for (Employee employee : employees) {
            for (LeaveType leaveType : activeLeaveTypes) {
                // Check if leave balance already exists for this employee, leave type, and year
                boolean exists = leaveBalanceRepository.existsByEmployeeAndLeaveTypeAndYear(
                        employee, leaveType, currentYear);

                if (!exists) {
                    LeaveBalance leaveBalance = createLeaveBalance(employee, leaveType, currentYear);
                    leaveBalanceRepository.save(leaveBalance);
                    createdCount++;
                } else {
                    // Optional: Update existing balance if needed
                    updatedCount++;
                }
            }
        }

        System.out.println("Leave balances initialized: " + createdCount + " created, " + updatedCount + " already exist");
    }

    private LeaveBalance createLeaveBalance(Employee employee, LeaveType leaveType, int year) {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setTotalDays(leaveType.getMaxDaysPerYear());
        leaveBalance.setUsedDays(0);
        leaveBalance.setCarryForwardDays(leaveType.getCarryForwardDays());
        leaveBalance.setYear(year);
        leaveBalance.calculateRemainingDays(); // Call the calculation method

        return leaveBalance;
    }
}