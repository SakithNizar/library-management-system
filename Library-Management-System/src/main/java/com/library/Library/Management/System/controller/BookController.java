package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Book;
import com.library.Library.Management.System.repository.BookRepository;
import com.library.Library.Management.System.response.ApiResponse;
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

    // ✅ GET all books
    @GetMapping
    public ApiResponse<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return new ApiResponse<>("Fetched all books successfully!", books);
    }

    // ✅ GET by ID
    @GetMapping("/{id}")
    public ApiResponse<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> new ApiResponse<>("Book found!", book))
                .orElseGet(() -> new ApiResponse<>("Book not found!", null));
    }

    // ✅ POST create
    @PostMapping
    public ApiResponse<Book> createBook(@RequestBody Book book) {
        Book saved = bookRepository.save(book);
        return new ApiResponse<>("Book added successfully!", saved);
    }

    // ✅ PUT update
    @PutMapping("/{id}")
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
                    return new ApiResponse<>("Book updated successfully!", saved);
                })
                .orElseGet(() -> new ApiResponse<>("Book not found!", null));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return new ApiResponse<>("Book deleted successfully!", "Deleted ID: " + id);
    }
}
