package com.example.booksAPI.services;

import com.example.booksAPI.dto.*;
import com.example.booksAPI.entities.Book;
import com.example.booksAPI.entities.Rating;
import com.example.booksAPI.enums.Publisher;
import com.example.booksAPI.exceptions.BadRequestException;
import com.example.booksAPI.exceptions.ResourceNotFoundException;
import com.example.booksAPI.repositories.BooksRepository;
import com.example.booksAPI.repositories.RatingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
@Service
public class BooksService {

    private BooksRepository booksRepository;
    private RatingsRepository ratingsRepository;

    public List<Book> getAllBooks() {
        return this.booksRepository.findAll();
    }

    public List<Book> getFilteredBooks(String title, String year, String author, String rating) {
        return this.booksRepository.getFilteredBooks(title, year, author, rating);
    }

    public Book getBookByID(int id) {
       return this.booksRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Book doesn't exist"));
    }

    public ResponseEntity<SuccessResponse> addBook(AddBookDTO newBook) {
        Book book = new Book();
        book.setAuthor(newBook.getAuthor());
        book.setPublication_year(Integer.parseInt(newBook.getYear()));
        book.setTitle(newBook.getTitle());
        book.setPublisher(Publisher.valueOf(newBook.getPublisher()));
        this.booksRepository.save(book);
        return ResponseEntity.ok(new SuccessResponse("Book added successfully"));
    }

    public ResponseEntity<SuccessResponse> rateBook(RateBookDTO ratings) {
        Set<Integer> bookIDs = ratings.getRatings().stream().map(BookRatingDTO::getId).collect(Collectors.toSet());
        List<Book> books = this.booksRepository.findAllById(bookIDs);
        Map<Integer, Book> bookMap = books.stream().collect(toMap(Book::getId, Function.identity()));
        List<Rating> newRatings = new ArrayList<>();
        ratings.getRatings().forEach(rating -> {
            Book book = bookMap.get(rating.getId());
            if(book == null) {
                throw new ResourceNotFoundException("Book with ID " + rating.getId() + " doesn't exist");
            }
            Rating newRating = new Rating();
            newRating.setScore(rating.getScore());
            newRating.setBook(book);
            book.getRatings().add(newRating);
            newRatings.add(newRating);
        });
        this.ratingsRepository.saveAll(newRatings);
        return ResponseEntity.ok(new SuccessResponse("Ratings added successfully"));
    }

    public ResponseEntity<SuccessResponse> updateBook(int id, UpdateBookDTO updatedBook) {
        Book foundBook = this.booksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book doesn't exist"));
        String newTitle = updatedBook.getTitle();
        String newYear = updatedBook.getYear();
        String newAuthor = updatedBook.getAuthor();
        if(newAuthor == null && newTitle == null && newYear == null) {
            throw new BadRequestException("No data provided for update");
        }
        if(newAuthor != null) {
            foundBook.setAuthor(updatedBook.getAuthor());
        }
        if(newTitle != null) {
            foundBook.setTitle(updatedBook.getTitle());
        }
        if(newYear != null) {
            foundBook.setPublication_year(Integer.parseInt(updatedBook.getYear()));
        }
        this.booksRepository.save(foundBook);
        return ResponseEntity.ok(new SuccessResponse("Book updated successfully"));
    }

    public ResponseEntity<SuccessResponse> deleteBook(int id) {
        if(!this.booksRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book doesn't exist");
        }
        this.booksRepository.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse("Book deleted successfully"));
    }
}
