package com.example.booksAPI.validation.validators;

import com.example.booksAPI.validation.annotations.IsYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<IsYear, String> {
    @Override
    public boolean isValid(String year, ConstraintValidatorContext constraintValidatorContext) {
        if(year != null) {
            return year.matches("^(19[0-9]{2}|20[0-9]{2})$");
        }
        return true;
    }
}
