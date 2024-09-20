package com.example.booksAPI.services;

import com.example.booksAPI.dto.*;
import com.example.booksAPI.entities.Book;
import com.example.booksAPI.exceptions.BadRequestException;
import com.example.booksAPI.exceptions.ResourceNotFoundException;
import com.example.booksAPI.repositories.BooksRepository;
import com.example.booksAPI.repositories.RatingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BooksServiceUnitTests {
    @Mock
    BooksRepository booksRepository;

    @Mock
    RatingsRepository ratingsRepository;

    @InjectMocks
    BooksService booksService;

    private List<Book> books;

    @BeforeEach
    public void setUp() {
        this.books = Arrays.asList(new Book(1, "Title 1", 2024, "Kamil", new ArrayList<>()),
                new Book(2, "Title 2", 2023, "Kamil", new ArrayList<>()));
    }

    @Test
    public void testGetAllBooks()  {
        when(this.booksRepository.findAll()).thenReturn(this.books);
        assertEquals(this.booksService.getAllBooks(), this.books);
    }

    @Test
    public void testGetFilteredBooks() {
        when(this.booksRepository.getFilteredBooks(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(this.books);
        assertEquals(this.booksService.getFilteredBooks(anyString(), anyString(), anyString(), anyString()), this.books);
    }

    @Test
    public void testGetBookByID_BookExist() {
        when(this.booksRepository.findById(anyInt())).thenReturn(Optional.ofNullable(this.books.getFirst()));
        assertEquals(this.booksService.getBookByID(anyInt()), this.books.getFirst());
    }

    @Test
    public void testGetBookByID_BookNotExist() {
        when(this.booksRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("Not found"));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            this.booksService.getBookByID(anyInt());
        });
        assertEquals(exception.getMessage(), "Not found");
    }

    @Test
    public void testAddBook() {
        Book newBook = new Book(1, "Title 2", 2023, "Kamil", new ArrayList<>());
        AddBookDTO newBookDTO = new AddBookDTO("Title 2", "2023", "Kamil");
        when(this.booksRepository.save(any(Book.class))).thenReturn(newBook);
        assertEquals(this.booksService.addBook(newBookDTO),
                ResponseEntity.ok(new SuccessResponse("Book added successfully")));
        verify(this.booksRepository).save(any(Book.class));
    }

    @Test
    public void testRateBook_AllBookFound() {
        RateBookDTO ratings = new RateBookDTO(
                Arrays.asList(new BookRatingDTO(1, 5), new BookRatingDTO(2, 3)));
        when(this.booksRepository.findAllById(anyIterable())).thenReturn(this.books);
        when(this.ratingsRepository.saveAll(anyIterable())).thenReturn(anyList());
        assertEquals(this.booksService.rateBook(ratings),
                ResponseEntity.ok(new SuccessResponse("Ratings added successfully")));
        verify(this.ratingsRepository).saveAll(anyIterable());
    }

    @Test
    public void testRateBook_NotFoundBook() {
        RateBookDTO ratings = new RateBookDTO(
                Arrays.asList(new BookRatingDTO(1, 5), new BookRatingDTO(2, 3)));
        when(this.booksRepository.findAllById(anyIterable())).thenReturn(new ArrayList<>());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            this.booksService.rateBook(ratings);
        });
        assertEquals(exception.getMessage(), "Book with ID 1 doesn't exist");
    }

    @Test
    public void testUpdateBook_ValidData() {
        when(this.booksRepository.findById(anyInt())).thenReturn(Optional.ofNullable(this.books.getFirst()));
        when(this.booksRepository.save(any(Book.class))).thenReturn(this.books.getFirst());
        UpdateBookDTO updateBookDTO = new UpdateBookDTO("Title 2", "2023", "Kamil");
        assertEquals(this.booksService.updateBook(1, updateBookDTO),
                ResponseEntity.ok(new SuccessResponse("Book updated successfully")));
    }

    @Test
    public void testUpdateBook_BookNotFound() {
        when(this.booksRepository.findById(anyInt())).thenReturn(Optional.empty());
        UpdateBookDTO updateBookDTO = new UpdateBookDTO("Title 2", "2023", "Kamil");
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            this.booksService.updateBook(1, updateBookDTO);
        });
        assertEquals(exception.getMessage(), "Book doesn't exist");
    }

    @Test
    public void testUpdateBook_EmptyDTO() {
        when(this.booksRepository.findById(anyInt())).thenReturn(Optional.ofNullable(this.books.getFirst()));
        UpdateBookDTO updateBookDTO = new UpdateBookDTO(null, null, null);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            this.booksService.updateBook(1, updateBookDTO);
        });
        assertEquals(exception.getMessage(), "No data provided for update");
    }

    @Test
    public void testDeleteBook_ValidID() {
        when(this.booksRepository.existsById(anyInt())).thenReturn(true);
        assertEquals(this.booksService.deleteBook(anyInt()),
                ResponseEntity.ok(new SuccessResponse("Book deleted successfully")));
        verify(this.booksRepository).deleteById(anyInt());
    }

    @Test
    public void testDeleteBook_InvalidID() {
        when(this.booksRepository.existsById(anyInt())).thenReturn(false);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            this.booksService.deleteBook(anyInt());
        });
        assertEquals(exception.getMessage(), "Book doesn't exist");
    }
}
