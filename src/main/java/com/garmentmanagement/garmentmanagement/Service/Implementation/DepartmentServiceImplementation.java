package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.DepartmentDto;
import com.garmentmanagement.garmentmanagement.Entity.Department;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Repository.DepartmentRepository;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImplementation implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        // Check duplicates
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new RuntimeException("Department name already exists: " + departmentDto.getName());
        }
        if (departmentRepository.existsByCode(departmentDto.getCode())) {
            throw new RuntimeException("Department code already exists: " + departmentDto.getCode());
        }

        Department department = modelMapper.map(departmentDto, Department.class);

        // Set department head if provided
        if (departmentDto.getDepartmentHeadId() != null && departmentDto.getDepartmentHeadId() > 0) {
            Employee departmentHead = employeeRepository.findById(departmentDto.getDepartmentHeadId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + departmentDto.getDepartmentHeadId()));
            department.setDepartmentHead(departmentHead);
        }

        // Set enum status
        if (departmentDto.getStatus() != null) {
            department.setStatus(Department.DepartmentStatus.valueOf(departmentDto.getStatus()));
        } else {
            department.setStatus(Department.DepartmentStatus.ACTIVE);
        }

        Department saved = departmentRepository.save(department);
        return convertToDto(saved);
    }

    @Override
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check duplicate name for other departments
        if (!existing.getName().equals(departmentDto.getName()) &&
                departmentRepository.existsByName(departmentDto.getName())) {
            throw new RuntimeException("Department name already exists: " + departmentDto.getName());
        }

        // Check duplicate code for other departments
        if (!existing.getCode().equals(departmentDto.getCode()) &&
                departmentRepository.existsByCode(departmentDto.getCode())) {
            throw new RuntimeException("Department code already exists: " + departmentDto.getCode());
        }

        // Update basic fields
        existing.setName(departmentDto.getName());
        existing.setCode(departmentDto.getCode());
        existing.setDescription(departmentDto.getDescription());
        existing.setLocation(departmentDto.getLocation());
        existing.setBudget(departmentDto.getBudget());
        existing.setEstablishedDate(departmentDto.getEstablishedDate());

        // Update status enum
        if (departmentDto.getStatus() != null) {
            existing.setStatus(Department.DepartmentStatus.valueOf(departmentDto.getStatus()));
        }

        // Update department head
        if (departmentDto.getDepartmentHeadId() != null && departmentDto.getDepartmentHeadId() > 0) {
            Employee departmentHead = employeeRepository.findById(departmentDto.getDepartmentHeadId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + departmentDto.getDepartmentHeadId()));
            existing.setDepartmentHead(departmentHead);
        } else {
            existing.setDepartmentHead(null);
        }

        Department updated = departmentRepository.save(existing);
        return convertToDto(updated);
    }

    @Override
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return convertToDto(department);
    }

    @Override
    public DepartmentDto getDepartmentByName(String name) {
        Department department = departmentRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Department not found with name: " + name));
        return convertToDto(department);
    }

    @Override
    public DepartmentDto getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Department not found with code: " + code));
        return convertToDto(department);
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDto> getDepartmentsByStatus(Department.DepartmentStatus status) {
        return departmentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDto> getDepartmentsByLocation(String location) {
        return departmentRepository.findByLocationContainingIgnoreCase(location)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDto assignDepartmentHead(Long departmentId, Long employeeId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));

        Employee departmentHead = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        department.setDepartmentHead(departmentHead);
        Department updated = departmentRepository.save(department);
        return convertToDto(updated);
    }

    @Override
    public void removeDepartmentHead(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));

        department.setDepartmentHead(null);
        departmentRepository.save(department);
    }

    @Override
    public Integer getEmployeeCount(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));

        return department.getEmployees().size();
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if department has employees
        if (!department.getEmployees().isEmpty()) {
            throw new RuntimeException("Cannot delete department with existing employees");
        }

        departmentRepository.delete(department);
    }

    private DepartmentDto convertToDto(Department department) {
        DepartmentDto dto = modelMapper.map(department, DepartmentDto.class);

        // Set department head info
        if (department.getDepartmentHead() != null) {
            dto.setDepartmentHeadId(department.getDepartmentHead().getId());
            dto.setDepartmentHeadName(
                    department.getDepartmentHead().getFirstName() + " " +
                            department.getDepartmentHead().getLastName()
            );
        }

        // Set calculated fields
        dto.setEmployeeCount(department.getEmployees().size());
        dto.setIsActive(department.getStatus() == Department.DepartmentStatus.ACTIVE);

        // Convert enum to string
        if (department.getStatus() != null) {
            dto.setStatus(department.getStatus().name());
        }

        return dto;
    }
}