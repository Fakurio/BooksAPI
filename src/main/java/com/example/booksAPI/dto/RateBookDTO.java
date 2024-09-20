package com.example.booksAPI.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateBookDTO {
    @NotEmpty(message = "cannot be empty")
    @Valid
    private List<BookRatingDTO> ratings;
}
