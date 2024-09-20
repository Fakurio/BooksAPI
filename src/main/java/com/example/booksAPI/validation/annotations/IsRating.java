package com.example.booksAPI.validation.annotations;

import com.example.booksAPI.validation.validators.RatingValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RatingValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsRating {
    String message() default "Rating must be in range 1-5";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}