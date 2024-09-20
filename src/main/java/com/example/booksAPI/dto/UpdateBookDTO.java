package com.example.booksAPI.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class UpdateBookDTO {
    @Length(min = 1, message = "cannot be empty")
    private String title;

    @Pattern(regexp = "^(19[0-9]{2}|20[0-9]{2})$", message = "it is not a year")
    private String year;

    @Length(min = 1, message = "cannot be empty")
    private String author;
}
