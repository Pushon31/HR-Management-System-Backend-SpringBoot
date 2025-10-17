package com.garmentmanagement.garmentmanagement.Config;

import com.garmentmanagement.garmentmanagement.Entity.Role;
import com.garmentmanagement.garmentmanagement.Entity.User;
import com.garmentmanagement.garmentmanagement.Repository.RoleRepository;
import com.garmentmanagement.garmentmanagement.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRoles();
        createAdminUser();
    }

    private void createRoles() {
        // Create roles if they don't exist
        createRoleIfNotFound(Role.ROLE_ADMIN, "System Administrator");
        createRoleIfNotFound(Role.ROLE_MANAGER, "Department Manager");
        createRoleIfNotFound(Role.ROLE_HR, "Human Resources Manager");
        createRoleIfNotFound(Role.ROLE_ACCOUNTANT, "Finance Accountant");
        createRoleIfNotFound(Role.ROLE_EMPLOYEE, "General Employee");
    }

    private void createRoleIfNotFound(String name, String description) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role(name, description);
            roleRepository.save(role);
            System.out.println("Created role: " + name);
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@garmentmanagement.com");
            admin.setFullName("System Administrator");

            Role adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            System.out.println("Default admin user created: admin / admin123");
        }
    }
}