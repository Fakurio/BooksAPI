package com.example.booksAPI;
import com.example.booksAPI.dto.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class BooksApiFunctionalTests {
    private static final Network network = Network.newNetwork();
    private static String token;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("BooksAPI")
            .withUsername("root")
            .withPassword("root")
            .withNetwork(network)
            .withNetworkAliases("mysql")
            .withMinimumRunningDuration(Duration.ofSeconds(5L));

    @Container
    private static final GenericContainer<?> appContainer = new GenericContainer<>(
            new ImageFromDockerfile().withFileFromPath(".", Paths.get("./"))
    )
            .withExposedPorts(8080)
            .withEnv("SPRING_DATASOURCE_USERNAME", "root")
            .withEnv("SPRING_DATASOURCE_PASSWORD", "root")
            .withEnv("SPRING_JPA_HIBERNATE_DDL-AUTO", "create")
            .withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://mysql:3306/BooksAPI")
            .withNetwork(network)
            .dependsOn(mysqlContainer);

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://" + appContainer.getHost() + ":" + appContainer.getMappedPort(8080);
    }

    @Order(1)
    @Test
    public void itShouldRegisterUser() {
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("test.testowy1@gmail.com", "nhtpn99@Z");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(registerUserDTO)
                .when().post("/auth/register")
                .then().statusCode(200)
                .body("message", is("User registered successfully"))
                .log().all();
    }

    @Order(2)
    @Test
    public void itShouldLogin() {
        LoginUserDTO loginUserDTO = new LoginUserDTO("test.testowy1@gmail.com", "nhtpn99@Z");

        token = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginUserDTO)
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().path("token");

        assertThat(token).isNotBlank();
    }

    @Order(3)
    @Test
    public void itShouldAddBook() {
        AddBookDTO addBookDTO = new AddBookDTO("Book", "2020", "Kamil", "UMCS");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(addBookDTO)
                .when().post("/books")
                .then().statusCode(200)
                .body("message", is("Book added successfully"))
                .log().all();

        RestAssured.get("/books")
                .then().statusCode(200)
                .body("size()", is(1))
                .body("[0].title", is("Book"))
                .body("[0].publication_year", is(2020))
                .body("[0].author", is("Kamil"))
                .body("[0].publisher", is("UMCS"));
    }

    @Order(4)
    @Test
    public void itShouldThrowExceptionWhenBookNotFound() {
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when().get("/books/10")
                .then().statusCode(404);
    }

    @Order(5)
    @Test
    public void itShouldUpdateBook() {
        UpdateBookDTO updateBookDTO = new UpdateBookDTO("Fancy book", null, null);

        RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(updateBookDTO)
                .when().put("/books/1")
                .then().statusCode(200)
                .body("message", is("Book updated successfully"))
                .log().all();

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when().get("/books/1")
                .then().statusCode(200)
                .body("title", is("Fancy book"))
                .body("publication_year", is(2020))
                .body("author", is("Kamil"))
                .body("publisher", is("UMCS"));
    }

    @Order(6)
    @Test
    public void itShouldAddRatingForBook() {
        RateBookDTO rateBookDTO = new RateBookDTO(List.of(new BookRatingDTO(1, 5)));

        RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(rateBookDTO)
                .when().post("/books/rating")
                .then().statusCode(200)
                .body("message", is("Ratings added successfully"))
                .log().all();

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when().get("/books/1")
                .then().statusCode(200)
                .body("ratings[0].score", is(5));
    }

    @Order(7)
    @Test
    public void itShouldDeleteBook() {
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when().delete("/books/1")
                .then().statusCode(200)
                .body("message", is("Book deleted successfully"))
                .log().all();

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when().get("/books/1")
                .then().statusCode(404);
    }
}
