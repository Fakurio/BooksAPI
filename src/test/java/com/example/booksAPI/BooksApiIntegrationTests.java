package com.example.booksAPI;

import com.example.booksAPI.dto.*;
import com.example.booksAPI.entities.Book;
import com.example.booksAPI.entities.Rating;
import com.example.booksAPI.enums.Publisher;
import com.example.booksAPI.repositories.BooksRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-integration-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BooksApiIntegrationTests {
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	BooksRepository booksRepository;


	@Order(1)
	@Test
	public void itShouldReturnAllBooks() throws Exception {
		this.mockMvc.perform(get("/books"))
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[0].title").value("To Kill a Mockingbird"));
	}

	@Test
	public void itShouldReturnFilteredBooks() throws Exception {
		//H2 is case-sensitive
		this.mockMvc.perform(get("/books/filter?title=The"))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].title").value("The Great Gatsby"))
				.andExpect(jsonPath("$[1].title").value("The Catcher in the Rye"))
				.andExpect(jsonPath("$[2].title").value("The Hobbit"));
	}

	@Test
	public void itShouldReturnBookById() throws Exception {
		Book found = booksRepository.findById(1).get();

		this.mockMvc.perform(get("/books/1"))
				.andExpect(jsonPath("$.title").value(found.getTitle()));
	}

	@Test
	public void itShouldThrowExceptionWhenBookNotFound() throws Exception {
		this.mockMvc.perform(get("/books/20"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void itShouldAddNewBook() throws Exception {
		AddBookDTO newBook = new AddBookDTO("Book", "2020", "Kamil", "UMCS");

		this.mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(newBook)))
				.andExpect(jsonPath("$.message").value("Book added successfully"));

		List<Book> books = this.booksRepository.findAll();
		Optional<Book> addedBook = books.stream().filter(book -> book.getTitle().equals("Book")).findFirst();
		assertThat(books).hasSize(11);
		assertThat(addedBook.isPresent()).isTrue();
		assertThat(addedBook.get().getTitle()).isEqualTo("Book");
	}

	@Test
	public void itShouldThrowExceptionWhenAddingBook_InvalidDTO() throws Exception {
		AddBookDTO newBook = new AddBookDTO("", "3000", "", "BENC");

		this.mockMvc.perform(post("/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.mapper.writeValueAsString(newBook)))
				.andExpect(jsonPath("$.title").value("cannot be empty"))
				.andExpect(jsonPath("$.year").value("it is not a valid year"))
				.andExpect(jsonPath("$.author").value("cannot be empty"))
				.andExpect(jsonPath("$.publisher").value("must be any of: 'POLLUB', 'UMCS', 'UP'"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	public void itShouldAddRatingForBook() throws Exception {
		RateBookDTO newRatings = new RateBookDTO(List.of(new BookRatingDTO(1, 5)));

		this.mockMvc.perform(post("/books/rating")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(newRatings)))
				.andExpect(jsonPath("$.message").value("Ratings added successfully"));

		Book book = this.booksRepository.findById(1).get();
		List<Rating> ratings = book.getRatings();
		assertThat(ratings).hasSize(1);
		assertThat(ratings.getFirst().getScore()).isEqualTo(5);
	}

	@Test
	public void itShouldThrowExceptionWhenAddingRatingForBook_EmptyList() throws Exception {
		RateBookDTO newRatings = new RateBookDTO(new ArrayList<>());

		this.mockMvc.perform(post("/books/rating")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.mapper.writeValueAsString(newRatings)))
				.andExpect(jsonPath("$.ratings").value("cannot be empty"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void itShouldThrowExceptionWhenAddingRatingForBook_InvalidRating() throws Exception {
		RateBookDTO newRatings = new RateBookDTO(List.of(new BookRatingDTO(1, 10)));

		this.mockMvc.perform(post("/books/rating")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.mapper.writeValueAsString(newRatings)))
				.andExpect(jsonPath("$['ratings[0].score']").value("must be in range 1-5"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void itShouldUpdateBook() throws Exception {
		UpdateBookDTO updateBookDTO = new UpdateBookDTO("Fancy book", null, null);
		Book bookBefore = this.booksRepository.findById(1).get();

		this.mockMvc.perform(put("/books/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(updateBookDTO)))
				.andExpect(jsonPath("$.message").value("Book updated successfully"));

		Book bookAfter = this.booksRepository.findById(1).get();
		assertThat(bookAfter.getTitle()).isEqualTo("Fancy book");
		assertThat(bookAfter.getPublication_year()).isEqualTo(bookBefore.getPublication_year());
		assertThat(bookAfter.getAuthor()).isEqualTo(bookBefore.getAuthor());
	}

	@Test
	public void itShouldThrowExceptionWhenUpdatingBook_BookNotFound() throws Exception {
		UpdateBookDTO updateBookDTO = new UpdateBookDTO("Fancy book", null, null);

		this.mockMvc.perform(put("/books/20")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.mapper.writeValueAsString(updateBookDTO)))
				.andExpect(jsonPath("$.message").value("Book doesn't exist"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void itShouldDeleteBook() throws Exception {
		Book book = new Book();
		book.setTitle("Test");
		book.setPublication_year(2024);
		book.setAuthor("Kamil");
		book.setPublisher(Publisher.POLLUB);
		Book savedBook = booksRepository.save(book);

		this.mockMvc.perform(delete("/books/{id}", savedBook.getId()))
				.andExpect(jsonPath("$.message").value("Book deleted successfully"));

		Optional<Book> found = booksRepository.findById(savedBook.getId());
		assertThat(found.isPresent()).isFalse();
	}
}
