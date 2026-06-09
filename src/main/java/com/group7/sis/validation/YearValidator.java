package com.group7.sis.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<ValidateYear, String> {
    @Override
    public boolean isValid(String year, ConstraintValidatorContext context){
        int startYear = Integer.parseInt(year.substring(0, 4));
        int endYear = Integer.parseInt(year.substring(5, 9));

        return startYear + 1 == endYear;
    }

}
