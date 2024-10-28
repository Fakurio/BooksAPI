package com.example.booksAPI.repositories;

import com.example.booksAPI.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
