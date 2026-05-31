package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.users.ProfileEntity.Sex;
import com.group7.sis.entity.users.ProfileEntity.CivilStatus;


@Getter
@Setter
public class ProfileDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private Sex sex;
    private String dateOfBirth;
    private CivilStatus civilStatus;
    private String nationality; 
    private String mobileNumber;
}
