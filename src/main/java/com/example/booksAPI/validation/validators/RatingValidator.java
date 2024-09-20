package com.example.booksAPI.validation.validators;

import com.example.booksAPI.validation.annotations.IsRating;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RatingValidator implements ConstraintValidator<IsRating, String> {
    @Override
    public boolean isValid(String rating, ConstraintValidatorContext constraintValidatorContext) {
        if(rating != null) {
            return rating.matches("^[1-5]$");
        }
        return true;
    }
}
