package com.example.booksAPI.services;

import com.example.booksAPI.dto.LoginSuccessResponse;
import com.example.booksAPI.dto.LoginUserDTO;
import com.example.booksAPI.dto.RegisterUserDTO;
import com.example.booksAPI.dto.SuccessResponse;
import com.example.booksAPI.entities.User;
import com.example.booksAPI.exceptions.BadRequestException;
import com.example.booksAPI.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ResponseEntity<SuccessResponse> register(RegisterUserDTO registerUserDTO) {
        User foundUser = usersRepository.findByEmail(registerUserDTO.getEmail()).orElse(null);
        if (foundUser != null) {
            throw new BadRequestException("User already exists");
        }
        User user = new User();
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        usersRepository.save(user);
        return ResponseEntity.ok(new SuccessResponse("User registered successfully"));
    }

    public ResponseEntity<LoginSuccessResponse> login(LoginUserDTO loginUserDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDTO.getEmail(),
                        loginUserDTO.getPassword()
                )
        );
        User user = usersRepository.findByEmail(loginUserDTO.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginSuccessResponse(token));
    }
}
