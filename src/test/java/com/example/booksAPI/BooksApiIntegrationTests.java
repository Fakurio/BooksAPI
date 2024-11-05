package com.example.booksAPI;

import com.example.booksAPI.dto.*;
import com.example.booksAPI.enums.Publisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BooksApiIntegrationTests {
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mockMvc;


	@Order(1)
	@Test
	public void testGetAllBooks() throws Exception {
		this.mockMvc.perform(get("/books"))
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[0].title").value("To Kill a Mockingbird"));
	}

	@Order(2)
	@Test
	public void testGetFilteredBooks() throws Exception {
		this.mockMvc.perform(get("/books/filter?title=the"))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].title").value("The Great Gatsby"));
	}

	@Order(3)
	@Test
	public void testGetFilteredBooks_EmptyParams() throws Exception {
		this.mockMvc.perform(get("/books/filter"))
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[9].title").value("Brave New World"));
	}

	@Order(4)
	@Test
	public void testGetBookByID() throws Exception {
		this.mockMvc.perform(get("/books/10"))
				.andExpect(jsonPath("$.title").value("Brave New World"));
	}

	@Order(5)
	@Test
	public void testAddBook() throws Exception {
		AddBookDTO newBook = new AddBookDTO("BOOOOM", "2020", "Kamil", Publisher.POLLUB);
		this.mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(newBook)))
				.andExpect(jsonPath("$.message").value("Book added successfully"));
		this.mockMvc.perform(get("/books/11"))
				.andExpect(jsonPath("$.title").value("BOOOOM"));
	}

	@Order(6)
	@Test
	public void testRateBook() throws Exception {
		RateBookDTO ratings = new RateBookDTO(List.of(new BookRatingDTO(11, 5)));
		this.mockMvc.perform(post("/books/rating")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(ratings)))
				.andExpect(jsonPath("$.message").value("Ratings added successfully"));
		this.mockMvc.perform(get("/books/filter?rating=5"))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title").value("BOOOOM"));
	}

	@Order(7)
	@Test
	public void testUpdateBook() throws Exception {
		UpdateBookDTO updateBookDTO = new UpdateBookDTO("BIG BOOOOM", null, null);
		this.mockMvc.perform(put("/books/11")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(updateBookDTO)))
				.andExpect(jsonPath("$.message").value("Book updated successfully"));
		this.mockMvc.perform(get("/books/11"))
				.andExpect(jsonPath("$.title").value("BIG BOOOOM"))
				.andExpect(jsonPath("$.year").value(2020))
				.andExpect(jsonPath("$.author").value("Kamil"));
	}

	@Order(8)
	@Test
	public void testDeleteBook() throws Exception {
		this.mockMvc.perform(delete("/books/11"))
				.andExpect(jsonPath("$.message").value("Book deleted successfully"));
		this.mockMvc.perform(get("/books/11"))
				.andExpect(jsonPath("$.message").value("Book doesn't exist"));
	}
}
