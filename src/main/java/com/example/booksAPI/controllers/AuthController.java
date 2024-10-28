package com.example.booksAPI.controllers;

import com.example.booksAPI.dto.LoginUserDTO;
import com.example.booksAPI.dto.RegisterUserDTO;
import com.example.booksAPI.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        return this.authService.register(registerUserDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginUserDTO loginUserDTO) {
        return this.authService.login(loginUserDTO);
    }
}
