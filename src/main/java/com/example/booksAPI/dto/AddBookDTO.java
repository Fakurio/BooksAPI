package com.example.booksAPI.dto;

import com.example.booksAPI.enums.Publisher;
import com.example.booksAPI.validation.annotations.ValueOfEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddBookDTO {
    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    private String title;

    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    @Pattern(regexp = "^(19[0-9]{2}|20[0-9]{2})$", message = "it is not a valid year")
    private String year;

    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    private String author;

    @ValueOfEnum(enumClass = Publisher.class, message = "must be any of: 'POLLUB', 'UMCS', 'UP'")
    @NotNull(message = "cannot be null")
    private String publisher;
}
