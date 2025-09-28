package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.UsersDto;
import com.garmentmanagement.garmentmanagement.Entity.Users;
import com.garmentmanagement.garmentmanagement.Repository.UserRepository;
import com.garmentmanagement.garmentmanagement.Service.UserService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserService {


    private final UserRepository userRepository;
    private final ModelMapper modelMapper;



    @Override
    public UsersDto createUser(UsersDto usersDto) {
        Users user =modelMapper.map(usersDto, Users.class);
        Users saved = userRepository.save(user);
        return modelMapper.map(saved, UsersDto.class);

    }
    @Override
    public UsersDto updateUser(Long id, UsersDto usersDto) {
        Users existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existing.setUsername(usersDto.getUsername());
        existing.setEmail(usersDto.getEmail());
        existing.setPassword(usersDto.getPassword());
        existing.setStatus(usersDto.getStatus());

        Users updated = userRepository.save(existing);
        return modelMapper.map(updated,UsersDto.class);

    }

    @Override
    public UsersDto getUserById(Long id) {
       Users user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
       return modelMapper.map(user, UsersDto.class);
    }

    @Override
    public List<UsersDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u->modelMapper.map(u,UsersDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        Users user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.deleteById(id);


    }
}
