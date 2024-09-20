package com.example.booksAPI.repositories;

import com.example.booksAPI.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BooksRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT b FROM Book b LEFT JOIN b.ratings r WHERE (:title IS NULL OR b.title LIKE %:title%) " +
            "AND (:year IS NULL OR b.year = :year) AND (:author IS NULL OR b.author LIKE %:author%) " +
            "AND (:rating IS NULL OR r.score = :rating)")
    List<Book> getFilteredBooks(@Param("title") String title, @Param("year") String year,
                                @Param("author") String author, @Param("rating") String rating);
}
