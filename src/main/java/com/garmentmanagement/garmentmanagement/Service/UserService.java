package com.garmentmanagement.garmentmanagement.Service;


import com.garmentmanagement.garmentmanagement.DTO.UsersDto;


import java.util.List;

public interface UserService {
    UsersDto createUser(UsersDto usersDto);


    UsersDto updateUser(Long id, UsersDto usersDto);

    UsersDto getUserById(Long id);
    List<UsersDto> getAllUsers();
    void deleteUser(Long id);

    // ✅ Optional: Add these methods for better functionality
//     UsersDto getUserByUsername(String username);
//     UsersDto getUserByEmail(String email);
//     boolean existsByUsername(String username);
//     boolean existsByEmail(String email);
}
