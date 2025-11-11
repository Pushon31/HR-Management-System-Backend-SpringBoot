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
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveTypeDataInitializer implements ApplicationRunner {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Check if leave types already exist
        if (leaveTypeRepository.count() == 0) {
            System.out.println("Creating default leave types...");

            List<LeaveType> defaultLeaveTypes = Arrays.asList(
                    createLeaveType("Sick Leave", "SL", LeaveType.LeaveCategory.SICK,
                            "Leave for health issues and medical treatment", 14, true, true, false, 7),

                    createLeaveType("Casual Leave", "CL", LeaveType.LeaveCategory.PAID,
                            "Casual leave for personal work", 10, true, true, false, 5),

                    createLeaveType("Annual Leave", "AL", LeaveType.LeaveCategory.PAID,
                            "Annual vacation leave", 21, true, true, true, 10),

                    createLeaveType("Maternity Leave", "ML", LeaveType.LeaveCategory.MATERNITY,
                            "Maternity leave for female employees", 180, true, true, false, 0),

                    createLeaveType("Paternity Leave", "PL", LeaveType.LeaveCategory.PATERNITY,
                            "Paternity leave for male employees", 15, true, true, false, 0),

                    createLeaveType("Emergency Leave", "EL", LeaveType.LeaveCategory.SPECIAL,
                            "Leave for emergency situations", 5, true, true, false, 0)
            );

            leaveTypeRepository.saveAll(defaultLeaveTypes);
            System.out.println("Default leave types created successfully!");
        } else {
            System.out.println("Leave types already exist in database.");
        }
    }

    private LeaveType createLeaveType(String name, String code, LeaveType.LeaveCategory category,
                                      String description, int maxDays, boolean isActive,
                                      boolean requiresApproval, boolean allowEncashment, int carryForward) {
        LeaveType leaveType = new LeaveType();
        leaveType.setName(name);
        leaveType.setCode(code);
        leaveType.setCategory(category);
        leaveType.setDescription(description);
        leaveType.setMaxDaysPerYear(maxDays);
        leaveType.setIsActive(isActive);
        leaveType.setRequiresApproval(requiresApproval);
        leaveType.setAllowEncashment(allowEncashment);
        leaveType.setCarryForwardDays(carryForward);
        return leaveType;
    }

    private void initializeYearlyBalances() {
        int currentYear = java.time.Year.now().getValue();
        int previousYear = currentYear - 1;

        // Check if we need to create balances for new year
        boolean currentYearBalancesExist = leaveBalanceRepository.countByYear(currentYear) > 0;

        if (!currentYearBalancesExist) {
            System.out.println("Initializing leave balances for year: " + currentYear);

            // Get balances from previous year for carry forward calculation
            List<LeaveBalance> previousYearBalances = leaveBalanceRepository.findByYear(previousYear);

            // Create new balances for current year
            List<Employee> employees = employeeRepository.findAll();
            List<LeaveType> activeLeaveTypes = leaveTypeRepository.findByIsActiveTrue();

            for (Employee employee : employees) {
                for (LeaveType leaveType : activeLeaveTypes) {
                    // Calculate carry forward from previous year
                    int carryForward = calculateCarryForward(previousYearBalances, employee, leaveType);

                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setEmployee(employee);
                    newBalance.setLeaveType(leaveType);
                    newBalance.setTotalDays(leaveType.getMaxDaysPerYear());
                    newBalance.setUsedDays(0);
                    newBalance.setCarryForwardDays(carryForward);
                    newBalance.setYear(currentYear);

                    leaveBalanceRepository.save(newBalance);
                }
            }

            System.out.println("Yearly leave balances initialized for " + currentYear);
        }
    }

    private int calculateCarryForward(List<LeaveBalance> previousBalances, Employee employee, LeaveType leaveType) {
        return previousBalances.stream()
                .filter(balance -> balance.getEmployee().equals(employee) &&
                        balance.getLeaveType().equals(leaveType))
                .findFirst()
                .map(balance -> Math.min(balance.getRemainingDays(), leaveType.getCarryForwardDays()))
                .orElse(0);
    }
}