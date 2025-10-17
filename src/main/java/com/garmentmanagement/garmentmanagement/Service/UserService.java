package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User updatedUser);
    User getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User deactivateUser(Long id);
    User activateUser(Long id);
}