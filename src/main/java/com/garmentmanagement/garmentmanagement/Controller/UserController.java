package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.UpdateRolesRequest;
import com.garmentmanagement.garmentmanagement.DTO.UserResponse;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Entity.Role;
import com.garmentmanagement.garmentmanagement.Entity.User;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Repository.RoleRepository;
import com.garmentmanagement.garmentmanagement.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    public UserController(UserRepository userRepository, RoleRepository roleRepository,
                          EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
    }

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