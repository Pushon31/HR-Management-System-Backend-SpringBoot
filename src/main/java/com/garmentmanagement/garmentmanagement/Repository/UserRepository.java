package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Role;
import com.garmentmanagement.garmentmanagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByRolesContaining(Role role);

    // ✅ NEW: Check if email exists for other users (exclude current user)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :excludeUserId")
    boolean existsByEmailAndIdNot(String email, Long excludeUserId);

    // ✅ NEW: Check if username exists for other users (exclude current user)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :excludeUserId")
    boolean existsByUsernameAndIdNot(String username, Long excludeUserId);
}
