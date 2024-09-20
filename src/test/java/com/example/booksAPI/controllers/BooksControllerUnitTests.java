package com.example.booksAPI.controllers;

import com.example.booksAPI.dto.*;
import com.example.booksAPI.entities.Book;
import com.example.booksAPI.services.BooksService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BooksController.class)
@AutoConfigureMockMvc
public class BooksControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BooksService booksService;

    private List<Book> books;
    private final SuccessResponse successResponse = new SuccessResponse("");

    @BeforeEach
    public void setUp() {
        this.books = Arrays.asList(new Book(1, "Title 1", 2024, "Kamil", new ArrayList<>()),
                new Book(2, "Title 2", 2023, "Kamil", new ArrayList<>()));
    }

    @Test
    public void testGetAllBooks() throws Exception {
        when(this.booksService.getAllBooks()).thenReturn(this.books);
        this.mockMvc.perform(get("/books"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    public void testGetBookByID_ValidID() throws Exception {
        when(this.booksService.getBookByID(1)).thenReturn(this.books.getFirst());
        this.mockMvc.perform(get("/books/1"))
                .andExpect(jsonPath("$.title").value("Title 1"));
    }

    @Test
    public void testGetBookByID_InvalidID() throws Exception {
        this.mockMvc.perform(get("/books/a")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID must be integer"));
    }

    @Test
    public void testAddBook_ValidData() throws Exception {
        AddBookDTO newBook = new AddBookDTO("Title 3", "2022", "Kamil");
        when(this.booksService.addBook(any(AddBookDTO.class))).thenReturn(ResponseEntity.ok(this.successResponse));
        this.mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(this.successResponse.getMessage()));
    }

    @Test
    public void testAddBook_InvalidData() throws Exception {
        AddBookDTO newBook = new AddBookDTO("", "2022222", "Kamil");
        this.mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(newBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("cannot be empty"))
                .andExpect(jsonPath("$.year").value("it is not a year"));
    }

    @Test
    public void testUpdateBook_ValidID() throws Exception {
        UpdateBookDTO updatedBook = new UpdateBookDTO("Benc", null, null);
        when(this.booksService.updateBook(anyInt(), any(UpdateBookDTO.class)))
                .thenReturn(ResponseEntity.ok(this.successResponse));
        this.mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(this.successResponse.getMessage()));
    }

    @Test
    public void testUpdateBook_InvalidID() throws Exception {
        UpdateBookDTO updatedBook = new UpdateBookDTO("Benc", null, null);
        this.mockMvc.perform(put("/books/a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID must be integer"));
    }

    @Test
    public void testUpdateBook_InvalidData() throws Exception {
        UpdateBookDTO updatedBook = new UpdateBookDTO("Benc", "ff", "");
        this.mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.year").value("it is not a year"))
                .andExpect(jsonPath("$.author").value("cannot be empty"));
    }

    @Test
    public void testDeleteBook_ValidID() throws Exception {
        when(this.booksService.deleteBook(1)).thenReturn(ResponseEntity.ok(successResponse));
        this.mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(this.successResponse.getMessage()));
    }

    @Test
    public void testDeleteBook_InvalidID() throws Exception {
        this.mockMvc.perform(delete("/books/a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID must be integer"));
    }

    @Test
    public void testRateBook_ValidData() throws Exception {
        RateBookDTO ratings = new RateBookDTO(
                Arrays.asList(new BookRatingDTO(1, 5), new BookRatingDTO(2, 3)));
        when(this.booksService.rateBook(any(RateBookDTO.class))).thenReturn(ResponseEntity.ok(this.successResponse));
        this.mockMvc.perform(post("/books/rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(ratings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(this.successResponse.getMessage()));
    }

    @Test
    public void testRateBook_Empty() throws Exception {
        RateBookDTO ratings = new RateBookDTO(new ArrayList<>());
        this.mockMvc.perform(post("/books/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(ratings)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ratings").value("cannot be empty"));
    }

    @Test
    public void testRateBook_InvalidData() throws Exception {
        RateBookDTO ratings = new RateBookDTO(
                Arrays.asList(new BookRatingDTO(1, 8), new BookRatingDTO(0, 3)));
        this.mockMvc.perform(post("/books/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(ratings)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['ratings[0].score']").value("must be in range 1-5"))
                .andExpect(jsonPath("$.['ratings[1].id']").value("must be greater than 0"));
    }

    @Test
    public void testGetFilteredBooks_ValidParameters() throws Exception {
        when(this.booksService.getFilteredBooks("ff", null, null, null)).thenReturn(this.books);
        this.mockMvc.perform(get("/books/filter?title=ff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(this.books.getFirst().getTitle()));
    }

    @Test
    public void testGetFilteredBooks_InvalidYear() throws Exception {
        this.mockMvc.perform(get("/books/filter?year=ff"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Invalid year"));
    }

    @Test
    public void testGetFilteredBooks_InvalidRating() throws Exception {
        this.mockMvc.perform(get("/books/filter?rating=6"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Rating must be in range 1-5"));
    }
}
