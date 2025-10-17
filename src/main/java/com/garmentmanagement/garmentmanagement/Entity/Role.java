package com.garmentmanagement.garmentmanagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.garmentmanagement.garmentmanagement.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true) // Only include specified fields
public class Role extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    @EqualsAndHashCode.Include // Explicitly include only this field
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore // Prevent JSON serialization issues
    private Set<User> users = new HashSet<>();

    // Predefined roles for garment system
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_HR = "ROLE_HR";
    public static final String ROLE_ACCOUNTANT = "ROLE_ACCOUNTANT";
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

    // Simple constructor
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
