package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.users.AccountEntity.Role;
import com.group7.sis.entity.users.AccountEntity.Status;

@Getter
@Setter
public class AccountDTO {
    private String username;
    private String email;
    private String password;
    private Role role;
    private Status status;
}
