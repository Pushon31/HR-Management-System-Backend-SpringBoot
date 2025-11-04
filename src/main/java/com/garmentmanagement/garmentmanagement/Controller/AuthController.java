package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.LoginRequest;
import com.garmentmanagement.garmentmanagement.DTO.SignupRequest;
import com.garmentmanagement.garmentmanagement.DTO.JwtResponse;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import com.garmentmanagement.garmentmanagement.Entity.Role;
import com.garmentmanagement.garmentmanagement.Entity.User;
import com.garmentmanagement.garmentmanagement.Repository.EmployeeRepository;
import com.garmentmanagement.garmentmanagement.Repository.RoleRepository;
import com.garmentmanagement.garmentmanagement.Repository.UserRepository;
import com.garmentmanagement.garmentmanagement.Security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the authenticated user
        User user = (User) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(user);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error: Username is already taken!")
            );
        }

        // Check if email exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Error: Email is already in use!")
            );
        }

        // Create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Set roles
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        boolean shouldCreateEmployee = false;
        String designation = "Employee";

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role: EMPLOYEE
            Role employeeRole = roleRepository.findByName(Role.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Error: Employee role not found."));
            roles.add(employeeRole);
            shouldCreateEmployee = true;
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
                        roles.add(adminRole);
                        break;
                    case "manager":
                        Role managerRole = roleRepository.findByName(Role.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Manager role not found."));
                        roles.add(managerRole);
                        break;
                    case "hr":
                        Role hrRole = roleRepository.findByName(Role.ROLE_HR)
                                .orElseThrow(() -> new RuntimeException("Error: HR role not found."));
                        roles.add(hrRole);
                        break;
                    case "accountant":
                        Role accountantRole = roleRepository.findByName(Role.ROLE_ACCOUNTANT)
                                .orElseThrow(() -> new RuntimeException("Error: Accountant role not found."));
                        roles.add(accountantRole);
                        break;
                    default:
                        Role employeeRole = roleRepository.findByName(Role.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Employee role not found."));
                        roles.add(employeeRole);
                }
            });

            // ✅ Check if any role requires employee record (exclude admin)
            List<String> employeeRoles = Arrays.asList("manager", "hr", "accountant", "employee");
            shouldCreateEmployee = strRoles.stream().anyMatch(employeeRoles::contains);

            // Set designation based on highest role
            if (strRoles.contains("manager")) designation = "Manager";
            else if (strRoles.contains("hr")) designation = "HR Manager";
            else if (strRoles.contains("accountant")) designation = "Accountant";
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // ✅ Auto-create employee record for employee roles (not for admin)
        Employee employee = null;
        if (shouldCreateEmployee) {
            employee = createEmployeeRecord(savedUser,designation);
        }

        // ✅ Return enhanced response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("userId", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("employeeCreated", shouldCreateEmployee);

        if (shouldCreateEmployee && employee != null) {
            response.put("employeeId", employee.getId());
            response.put("employeeCode", employee.getEmployeeId());
            response.put("designation", employee.getDesignation());
        }

        return ResponseEntity.ok(response);
    }

    // ✅ Enhanced employee creation method
    private Employee createEmployeeRecord(User user, String designation) {
        try {
            // Generate unique employee ID
            String employeeId = "EMP" + System.currentTimeMillis();

            // Create employee entity
            Employee employee = new Employee();
            employee.setFirstName(user.getFullName().split(" ")[0]);
            employee.setLastName(user.getFullName().contains(" ") ?
                    user.getFullName().substring(user.getFullName().indexOf(" ") + 1) : "");
            employee.setEmployeeId(employeeId);
            employee.setEmail(user.getEmail());
            employee.setDesignation(designation);
            employee.setStatus(Employee.EmployeeStatus.ACTIVE);
            employee.setWorkType(Employee.EmployeeWorkType.ONSITE);
            employee.setEmployeeType(Employee.EmployeeType.FULL_TIME);
            employee.setJoinDate(LocalDate.now());

            // ✅ Set user reference
            employee.setUser(user);

            Employee savedEmployee = employeeRepository.save(employee);

            // ✅ Update user with employee reference (bidirectional relationship)
            user.setEmployee(savedEmployee);
            userRepository.save(user);

            System.out.println("✅ Auto-created employee record for: " + user.getEmail() + " with designation: " + designation);
            return savedEmployee;
        } catch (Exception e) {
            System.err.println("❌ Failed to auto-create employee record: " + e.getMessage());
            return null;
        }
    }



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(null,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                roles));
    }
}