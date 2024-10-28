package com.example.booksAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginSuccessResponse {
    private String token;
}
