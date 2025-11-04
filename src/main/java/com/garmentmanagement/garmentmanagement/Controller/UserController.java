package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.CreateUserRequest;
import com.garmentmanagement.garmentmanagement.DTO.UpdateRolesRequest;
import com.garmentmanagement.garmentmanagement.DTO.UserResponse;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Entity.Role;
import com.garmentmanagement.garmentmanagement.Entity.User;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Repository.RoleRepository;
import com.garmentmanagement.garmentmanagement.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;



    // ✅ Get all users with their roles
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    // ✅ Update user roles
    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody UpdateRolesRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get the requested roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoleNames()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User roles updated successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody CreateUserRequest updateRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Update username - exclude current user from check
            if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(user.getUsername())) {
                boolean usernameExists = userRepository.existsByUsernameAndIdNot(updateRequest.getUsername(), userId);
                if (usernameExists) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Error: Username is already taken by another user!")
                    );
                }
                user.setUsername(updateRequest.getUsername());
            }

            // Update email - exclude current user from check
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
                boolean emailExists = userRepository.existsByEmailAndIdNot(updateRequest.getEmail(), userId);
                if (emailExists) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Error: Email is already in use by another user!")
                    );
                }
                user.setEmail(updateRequest.getEmail());
            }

            // Update full name
            if (updateRequest.getFullName() != null) {
                user.setFullName(updateRequest.getFullName());
            }

            // Update password if provided and not empty
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }

            // Update roles if provided
            if (updateRequest.getRoles() != null && !updateRequest.getRoles().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                for (String roleName : updateRequest.getRoles()) {
                    Role role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    roles.add(role);
                }
                user.setRoles(roles);
            }

            User updatedUser = userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully!",
                    "userId", updatedUser.getId(),
                    "username", updatedUser.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error updating user: " + e.getMessage())
            );
        }
    }

    // ✅ Assign role to user
    @PostMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<?> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);

        return ResponseEntity.ok("Role " + roleName + " assigned to user");
    }

    // ✅ Remove role from user
    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<?> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.getRoles().remove(role);
        userRepository.save(user);

        return ResponseEntity.ok("Role " + roleName + " removed from user");
    }

    // ✅ Get users by role
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        List<User> users = userRepository.findByRolesContaining(role);
        List<UserResponse> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            // Check if username exists
            if (userRepository.existsByUsername(createUserRequest.getUsername())) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Error: Username is already taken!")
                );
            }

            // Check if email exists
            if (userRepository.existsByEmail(createUserRequest.getEmail())) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Error: Email is already in use!")
                );
            }

            // Create new user
            User user = new User();
            user.setUsername(createUserRequest.getUsername());
            user.setEmail(createUserRequest.getEmail());
            user.setFullName(createUserRequest.getFullName());
            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            user.setIsActive(true);

            // Set roles
            Set<Role> roles = new HashSet<>();
            for (String roleName : createUserRequest.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);

            User savedUser = userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully!",
                    "userId", savedUser.getId(),
                    "username", savedUser.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error creating user: " + e.getMessage())
            );
        }
    }

    // UserController.java - createEmployeeForUser method update করুন
    @PostMapping("/{userId}/create-employee")
    public ResponseEntity<?> createEmployeeForUser(@PathVariable Long userId, @RequestBody(required = false) Map<String, String> employeeData) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Check if user already has employee
            if (user.getEmployee() != null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "User already has an employee record")
                );
            }

            // Check if user has roles that require employee
            List<String> employeeRoles = Arrays.asList(Role.ROLE_EMPLOYEE, Role.ROLE_MANAGER, Role.ROLE_HR, Role.ROLE_ACCOUNTANT);
            boolean hasEmployeeRole = user.getRoles().stream()
                    .anyMatch(role -> employeeRoles.contains(role.getName()));

            if (!hasEmployeeRole) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "User roles don't require employee record. Add employee role first.")
                );
            }

            // Generate unique employee ID
            String employeeId = "EMP" + System.currentTimeMillis();

            // Create employee
            Employee employee = new Employee();
            employee.setFirstName(user.getFullName().split(" ")[0]);
            employee.setLastName(user.getFullName().contains(" ") ?
                    user.getFullName().substring(user.getFullName().indexOf(" ") + 1) : "");
            employee.setEmployeeId(employeeId);
            employee.setEmail(user.getEmail());

            // Flexible status, work type, employee type
            if (employeeData != null && employeeData.containsKey("status")) {
                employee.setStatus(Employee.EmployeeStatus.valueOf(employeeData.get("status")));
            } else {
                employee.setStatus(Employee.EmployeeStatus.ACTIVE);
            }

            if (employeeData != null && employeeData.containsKey("workType")) {
                employee.setWorkType(Employee.EmployeeWorkType.valueOf(employeeData.get("workType")));
            } else {
                employee.setWorkType(Employee.EmployeeWorkType.ONSITE);
            }

            if (employeeData != null && employeeData.containsKey("employeeType")) {
                employee.setEmployeeType(Employee.EmployeeType.valueOf(employeeData.get("employeeType")));
            } else {
                employee.setEmployeeType(Employee.EmployeeType.FULL_TIME);
            }

            employee.setJoinDate(LocalDate.now());

            // Set designation based on role or from request
            if (employeeData != null && employeeData.containsKey("designation")) {
                employee.setDesignation(employeeData.get("designation"));
            } else {
                if (user.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_MANAGER))) {
                    employee.setDesignation("Manager");
                } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_HR))) {
                    employee.setDesignation("HR Manager");
                } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_ACCOUNTANT))) {
                    employee.setDesignation("Accountant");
                } else {
                    employee.setDesignation("Employee");
                }
            }

            Employee savedEmployee = employeeRepository.save(employee);

            return ResponseEntity.ok(Map.of(
                    "message", "Employee record created successfully",
                    "employeeId", savedEmployee.getId(),
                    "employeeCode", savedEmployee.getEmployeeId(),
                    "designation", savedEmployee.getDesignation(),
                    "status", savedEmployee.getStatus().name(),
                    "workType", savedEmployee.getWorkType().name(),
                    "employeeType", savedEmployee.getEmployeeType().name()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error creating employee: " + e.getMessage())
            );
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        try {
            System.out.println("🔍 UserController: Getting user by ID: " + userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            UserResponse response = convertToUserResponse(user);

            System.out.println("✅ UserController: Returning user: " + response.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ UserController: Error getting user by ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setActive(user.getIsActive());

        // Get role names
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        response.setRoles(roleNames);

        // Get employee info if exists
        Optional<Employee> employee = employeeRepository.findByEmail(user.getEmail());
        if (employee.isPresent()) {
            response.setEmployeeId(employee.get().getId());
            response.setEmployeeCode(employee.get().getEmployeeId());
            response.setDesignation(employee.get().getDesignation());
            response.setDepartmentName(employee.get().getDepartment() != null ?
                    employee.get().getDepartment().getName() : "No Department");
        }

        return response;
    }
}