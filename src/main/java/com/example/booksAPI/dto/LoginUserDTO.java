package com.example.booksAPI.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginUserDTO {
    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    private String email;

    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    private String password;
}
