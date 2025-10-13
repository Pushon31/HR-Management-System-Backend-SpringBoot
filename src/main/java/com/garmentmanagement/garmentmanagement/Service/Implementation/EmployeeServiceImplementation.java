package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.EmployeeDto;
import com.garmentmanagement.garmentmanagement.Entity.Department;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Repository.DepartmentRepository;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImplementation implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        // Check duplicates
        if (employeeRepository.existsByEmployeeId(employeeDto.getEmployeeId())) {
            throw new RuntimeException("Employee already exists with id: " + employeeDto.getEmployeeId());
        }
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + employeeDto.getEmail());
        }
        if (employeeDto.getNidNumber() != null && employeeRepository.existsByNidNumber(employeeDto.getNidNumber())) {
            throw new RuntimeException("NID already exists: " + employeeDto.getNidNumber());
        }

        // Create new employee manually
        Employee employee = new Employee();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmployeeId(employeeDto.getEmployeeId());
        employee.setEmail(employeeDto.getEmail());
        employee.setNidNumber(employeeDto.getNidNumber());
        employee.setBankAccountNumber(employeeDto.getBankAccountNumber());
        employee.setBirthDate(employeeDto.getBirthDate());
        employee.setJoinDate(employeeDto.getJoinDate());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setEmergencyContact(employeeDto.getEmergencyContact());
        employee.setAddress(employeeDto.getAddress());
        employee.setDesignation(employeeDto.getDesignation());
        employee.setShift(employeeDto.getShift());
        employee.setBasicSalary(employeeDto.getBasicSalary());
        employee.setProfilePic(employeeDto.getProfilePic());

        // ✅ FIX: Check for valid departmentId (not null and > 0)
        if (employeeDto.getDepartmentId() != null && employeeDto.getDepartmentId() > 0) {
            Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + employeeDto.getDepartmentId()));
            employee.setDepartment(department);
        }

        // ✅ FIX: Check for valid managerId (not null and > 0)
        if (employeeDto.getManagerId() != null && employeeDto.getManagerId() > 0) {
            Employee manager = employeeRepository.findById(employeeDto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + employeeDto.getManagerId()));
            employee.setManager(manager);
        }

        // Set enums manually
        if (employeeDto.getGender() != null) {
            employee.setGender(Employee.Gender.valueOf(employeeDto.getGender()));
        }
        if (employeeDto.getMaritalStatus() != null) {
            employee.setMaritalStatus(Employee.MaritalStatus.valueOf(employeeDto.getMaritalStatus()));
        }
        if (employeeDto.getEmployeeType() != null) {
            employee.setEmployeeType(Employee.EmployeeType.valueOf(employeeDto.getEmployeeType()));
        }
        if (employeeDto.getStatus() != null) {
            employee.setStatus(Employee.EmployeeStatus.valueOf(employeeDto.getStatus()));
        } else {
            employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        }

        Employee saved = employeeRepository.save(employee);
        return convertToDto(saved);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Check duplicates only if values are changing
        if (!existing.getEmployeeId().equals(employeeDto.getEmployeeId()) &&
                employeeRepository.existsByEmployeeId(employeeDto.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + employeeDto.getEmployeeId());
        }
        if (!existing.getEmail().equals(employeeDto.getEmail()) &&
                employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + employeeDto.getEmail());
        }
        if (employeeDto.getNidNumber() != null &&
                !employeeDto.getNidNumber().equals(existing.getNidNumber()) &&
                employeeRepository.existsByNidNumber(employeeDto.getNidNumber())) {
            throw new RuntimeException("NID already exists: " + employeeDto.getNidNumber());
        }

        // Update basic fields
        existing.setFirstName(employeeDto.getFirstName());
        existing.setLastName(employeeDto.getLastName());
        existing.setEmployeeId(employeeDto.getEmployeeId());
        existing.setEmail(employeeDto.getEmail());
        existing.setNidNumber(employeeDto.getNidNumber());
        existing.setBankAccountNumber(employeeDto.getBankAccountNumber());
        existing.setBirthDate(employeeDto.getBirthDate());
        existing.setJoinDate(employeeDto.getJoinDate());
        existing.setPhoneNumber(employeeDto.getPhoneNumber());
        existing.setEmergencyContact(employeeDto.getEmergencyContact());
        existing.setAddress(employeeDto.getAddress());
        existing.setDesignation(employeeDto.getDesignation());
        existing.setShift(employeeDto.getShift());
        existing.setBasicSalary(employeeDto.getBasicSalary());
        existing.setProfilePic(employeeDto.getProfilePic());

        // ✅ FIX: Update department only if valid ID
        if (employeeDto.getDepartmentId() != null && employeeDto.getDepartmentId() > 0) {
            Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + employeeDto.getDepartmentId()));
            existing.setDepartment(department);
        } else {
            existing.setDepartment(null);
        }

        // ✅ FIX: Update manager only if valid ID
        if (employeeDto.getManagerId() != null && employeeDto.getManagerId() > 0) {
            Employee manager = employeeRepository.findById(employeeDto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + employeeDto.getManagerId()));
            existing.setManager(manager);
        } else {
            existing.setManager(null);
        }

        // Update enums
        if (employeeDto.getGender() != null) {
            existing.setGender(Employee.Gender.valueOf(employeeDto.getGender()));
        }
        if (employeeDto.getMaritalStatus() != null) {
            existing.setMaritalStatus(Employee.MaritalStatus.valueOf(employeeDto.getMaritalStatus()));
        }
        if (employeeDto.getEmployeeType() != null) {
            existing.setEmployeeType(Employee.EmployeeType.valueOf(employeeDto.getEmployeeType()));
        }
        if (employeeDto.getStatus() != null) {
            existing.setStatus(Employee.EmployeeStatus.valueOf(employeeDto.getStatus()));
        }

        Employee updated = employeeRepository.save(existing);
        return convertToDto(updated);
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return convertToDto(employee);
    }

    @Override
    public EmployeeDto getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        return convertToDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByType(Employee.EmployeeType employeeType) {
        return employeeRepository.findByEmployeeType(employeeType)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByDesignation(String designation) {
        return employeeRepository.findByDesignation(designation)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByStatus(Employee.EmployeeStatus status) {
        return employeeRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getManagerTeam(Long managerId) {
        return employeeRepository.findSubordinatesByManagerId(managerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void assignManager(Long employeeId, Long managerId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        employee.setManager(manager);
        employeeRepository.save(employee);
    }

    @Override
    public EmployeeDto getEmployeeManager(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (employee.getManager() == null) {
            throw new RuntimeException("Employee has no manager assigned");
        }
        return convertToDto(employee.getManager());
    }

    @Override
    public List<EmployeeDto> getEmployeesWithoutManager() {
        return employeeRepository.findEmployeesWithoutManager()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }
    @Override
    public Map<Employee.EmployeeWorkType, Long> getEmployeeWorkTypeStats() {
        return employeeRepository.countEmployeesByWorkType();
    }

    // ✅ Use ModelMapper only for basic field mapping in convertToDto
    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = modelMapper.map(employee, EmployeeDto.class);

        // ✅ Manual mapping for additional fields
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartmentName(employee.getDepartment().getName());
        }

        if (employee.getManager() != null) {
            dto.setManagerId(employee.getManager().getId());
            dto.setManagerName(employee.getManager().getFirstName() + " " + employee.getManager().getLastName());
        }

        // ✅ Convert enums to string
        if (employee.getGender() != null) {
            dto.setGender(employee.getGender().name());
        }
        if (employee.getMaritalStatus() != null) {
            dto.setMaritalStatus(employee.getMaritalStatus().name());
        }
        if (employee.getEmployeeType() != null) {
            dto.setEmployeeType(employee.getEmployeeType().name());
        }
        if (employee.getStatus() != null) {
            dto.setStatus(employee.getStatus().name());
        }
        // ✅ NEW: Work Type mapping
        if (employee.getWorkType() != null) {
            dto.setWorkType(employee.getWorkType().name());
        }


        return dto;
    }



}