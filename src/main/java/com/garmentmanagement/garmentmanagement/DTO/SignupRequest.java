package com.garmentmanagement.garmentmanagement.DTO;

import lombok.Data;
import java.util.Set;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String fullName;
    private String password;
    private Set<String> roles;
}