package com.garmentmanagement.garmentmanagement.Config;

import com.garmentmanagement.garmentmanagement.DTO.EmployeeDto;
import com.garmentmanagement.garmentmanagement.Entity.Employee;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        // ✅ EmployeeDto to Employee mapping customization
        modelMapper.typeMap(EmployeeDto.class, Employee.class)
                .addMappings(mapper -> {
                    mapper.skip(Employee::setManager);   // Skip manager during mapping
                    mapper.skip(Employee::setDepartment); // Skip department during mapping
                });

        return modelMapper;
    }
}
