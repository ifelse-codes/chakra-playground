package com.example.bookapp;

import com.example.bookapp.model.Book;
import com.example.bookapp.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    // ── Ping ─────────────────────────────────────────────────────────────────

    @Test
    void ping_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/books/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.service").value("book-app"));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Test
    void createBook_shouldReturn201() throws Exception {
        Book book = new Book("Clean Code", "Robert Martin", "9780132350884", 39.99, 2008);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert Martin"));
    }

    @Test
    void createBook_withMissingTitle_shouldReturn400() throws Exception {
        Book book = new Book(null, "Some Author", "123", 9.99, 2020);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest());
    }

    // ── Get All ───────────────────────────────────────────────────────────────

    @Test
    void getAllBooks_shouldReturnList() throws Exception {
        bookRepository.save(new Book("Book A", "Author A", "ISBN-A", 10.0, 2020));
        bookRepository.save(new Book("Book B", "Author B", "ISBN-B", 20.0, 2021));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ── Get By Id ─────────────────────────────────────────────────────────────

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        Book saved = bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", 49.99, 2018));

        mockMvc.perform(get("/api/books/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"));
    }

    @Test
    void getBookById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/books/9999"))
                .andExpect(status().isNotFound());
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    void searchByAuthor_shouldReturnMatchingBooks() throws Exception {
        bookRepository.save(new Book("Book 1", "Martin Fowler", "ISBN-1", 35.0, 2019));
        bookRepository.save(new Book("Book 2", "Martin Fowler", "ISBN-2", 40.0, 2021));
        bookRepository.save(new Book("Book 3", "Other Author", "ISBN-3", 25.0, 2020));

        mockMvc.perform(get("/api/books/search").param("author", "Martin Fowler"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].author").value("Martin Fowler"));
    }

    @Test
    void searchByTitle_shouldReturnMatchingBooks() throws Exception {
        bookRepository.save(new Book("Spring in Action", "Craig Walls", "ISBN-X", 45.0, 2022));
        bookRepository.save(new Book("Spring Boot Up", "Mark Heckler", "ISBN-Y", 50.0, 2021));
        bookRepository.save(new Book("Java Basics", "Author Z", "ISBN-Z", 20.0, 2020));

        mockMvc.perform(get("/api/books/search").param("title", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    void updateBook_shouldReturnUpdated() throws Exception {
        Book saved = bookRepository.save(new Book("Old Title", "Old Author", "ISBN-OLD", 10.0, 2000));
        Book updated = new Book("New Title", "New Author", "ISBN-NEW", 29.99, 2023);

        mockMvc.perform(put("/api/books/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    void deleteBook_shouldReturn204() throws Exception {
        Book saved = bookRepository.save(new Book("To Delete", "Author", "ISBN-DEL", 5.0, 2010));

        mockMvc.perform(delete("/api/books/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    // ── ISBN ──────────────────────────────────────────────────────────────────

    @Test
    void getByIsbn_shouldReturnBook() throws Exception {
        bookRepository.save(new Book("ISBN Test Book", "Author", "978-TEST-ISBN", 15.0, 2022));

        mockMvc.perform(get("/api/books/isbn/978-TEST-ISBN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("ISBN Test Book"));
    }
}
