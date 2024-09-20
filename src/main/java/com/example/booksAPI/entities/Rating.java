package com.example.booksAPI.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Table(name = "ratings")
@Data
@Entity
public class Rating {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "score", columnDefinition = "INT CHECK (score >= 1 AND score <= 5)")
    private Integer score;

    @JsonIgnore
    @ManyToOne
    private Book book;
}
