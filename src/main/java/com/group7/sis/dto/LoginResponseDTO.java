package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private boolean success;
    private Long id;
    private String role;
    private String name;
    private String message;
}
