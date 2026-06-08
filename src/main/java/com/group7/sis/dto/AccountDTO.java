package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.users.AccountEntity.Role;
import com.group7.sis.entity.users.AccountEntity.Status;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class AccountDTO {

    @NotEmpty(message = "Username cannot be empty")
    private String username;
    
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotNull(message = "Please define account role")
    private Role role;

    @NotNull(message = "Please define account status")
    private Status status;
}
