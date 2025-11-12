package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Book;
import com.library.Library.Management.System.repository.BookRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // Optional if connecting from React later
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ✅ GET all books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // ✅ GET book by ID
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // ✅ POST create new book
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    // ✅ PUT update book
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setGenre(updatedBook.getGenre());
                    book.setLanguage(updatedBook.getLanguage());
                    book.setStatus(updatedBook.getStatus());
                    book.setIsbn(updatedBook.getIsbn());
                    return bookRepository.save(book);
                })
                .orElse(null);
    }

    // ✅ DELETE book
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }
}