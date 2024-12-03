package com.example.booksAPI.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserDTO {
    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    private String email;

    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    private String password;
}
