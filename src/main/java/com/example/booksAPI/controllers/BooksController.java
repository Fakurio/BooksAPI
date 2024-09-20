package com.example.booksAPI.controllers;
import com.example.booksAPI.dto.AddBookDTO;
import com.example.booksAPI.dto.RateBookDTO;
import com.example.booksAPI.dto.UpdateBookDTO;
import com.example.booksAPI.entities.Book;
import com.example.booksAPI.exceptions.BadRequestException;
import com.example.booksAPI.services.BooksService;
import com.example.booksAPI.validation.annotations.IsRating;
import com.example.booksAPI.validation.annotations.IsYear;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/books")
@AllArgsConstructor
@Validated
public class BooksController {

    private BooksService booksService;


    @GetMapping("/filter")
    public List<Book> getFilteredBooks(@RequestParam(required = false) String title,
                                       @IsYear @RequestParam(required = false) String year,
                                       @RequestParam(required = false) String author,
                                       @IsRating @RequestParam(required = false) String rating) {
        return this.booksService.getFilteredBooks(title, year, author, rating);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return this.booksService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookByID(@PathVariable String id) {
        if (!id.matches("\\d+")) {
            throw new BadRequestException("ID must be integer");
        }
        return this.booksService.getBookByID(Integer.parseInt(id));
    }

    @PostMapping()
    public ResponseEntity<?> addBook(@RequestBody @Valid AddBookDTO newBook) {
        return this.booksService.addBook(newBook);
    }

    @PostMapping("/rating")
    public ResponseEntity<?> rateBook(@RequestBody @Valid RateBookDTO ratings) {
        return this.booksService.rateBook(ratings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable String id, @RequestBody @Valid UpdateBookDTO updatedBook) {
        if (!id.matches("\\d+")) {
            throw new BadRequestException("ID must be integer");
        }
        return this.booksService.updateBook(Integer.parseInt(id), updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        if (!id.matches("\\d+")) {
            throw new BadRequestException("ID must be integer");
        }
        return this.booksService.deleteBook(Integer.parseInt(id));
    }
}
