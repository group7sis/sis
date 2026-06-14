package com.group7.sis.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDTO {
    private Long id;
    
    @NotEmpty(message = "Department name is required")
    private String name;

    @NotEmpty(message = "Department code is required")
    private String code;
}
