package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Book;
import com.library.Library.Management.System.repository.BookRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // GET all books (USER or LIBRARIAN)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return new ApiResponse<>("✅ Fetched all books successfully!", books);
    }

    // GET a specific book by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> new ApiResponse<>("📘 Book found!", book))
                .orElseGet(() -> new ApiResponse<>("❌ Book not found!", null));
    }

    // ADD a new book (LIBRARIAN only)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<Book> createBook(@RequestBody Book book) {
        Book saved = bookRepository.save(book);
        return new ApiResponse<>("✅ Book added successfully!", saved);
    }

    // UPDATE a book (LIBRARIAN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setGenre(updatedBook.getGenre());
                    book.setLanguage(updatedBook.getLanguage());
                    book.setStatus(updatedBook.getStatus());
                    book.setIsbn(updatedBook.getIsbn());
                    Book saved = bookRepository.save(book);
                    return new ApiResponse<>("✅ Book updated successfully!", saved);
                })
                .orElseGet(() -> new ApiResponse<>("❌ Book not found!", null));
    }

    // DELETE a book (LIBRARIAN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<String> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return new ApiResponse<>("❌ Book not found!", "ID: " + id);
        }
        bookRepository.deleteById(id);
        return new ApiResponse<>("🗑️ Book deleted successfully!", "Deleted ID: " + id);
    }
}

