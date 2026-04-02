package com.example.bookapp.controller;

import com.example.bookapp.model.Book;
import com.example.bookapp.service.BookService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books
    @GetMapping
    public List<Book> getAll() {
        return bookService.findAll();
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    // GET /api/books/isbn/{isbn}
    @GetMapping("/isbn/{isbn}")
    public Book getByIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn);
    }

    // GET /api/books/search?author=&title=&year=
    @GetMapping("/search")
    public List<Book> search(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year) {

        if (author != null) return bookService.findByAuthor(author);
        if (title != null)  return bookService.findByTitle(title);
        if (year != null)   return bookService.findByYear(year);

        return bookService.findAll();
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody Book book) {
        Book saved = bookService.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/books/{id}
    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return bookService.update(id, book);
    }

    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/books/ping  – lightweight test endpoint
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "service", "book-app");
    }
}
