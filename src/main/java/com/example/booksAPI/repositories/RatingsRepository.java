package com.example.booksAPI.repositories;

import com.example.booksAPI.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingsRepository extends JpaRepository<Rating, Integer> {
}
