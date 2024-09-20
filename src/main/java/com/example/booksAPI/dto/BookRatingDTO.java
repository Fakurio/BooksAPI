package com.example.booksAPI.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookRatingDTO {
    @Positive
    @NotNull
    private Integer id;

    @Min(value = 1, message = "must be in range 1-5")
    @Max(value = 5, message = "must be in range 1-5")
    @NotNull
    private Integer score;
}
