package com.garmentmanagement.garmentmanagement.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRolesRequest {
    private List<String> roleNames;
}
