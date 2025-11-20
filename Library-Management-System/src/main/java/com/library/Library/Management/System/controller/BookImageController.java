package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Book;
import com.library.Library.Management.System.repository.BookRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookImageController {

    @Autowired
    private BookRepository bookRepository;

    // Upload book cover - librarian only
    @PostMapping("/upload-cover/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<String> uploadCover(@PathVariable Long bookId,
                                           @RequestParam("file") MultipartFile file) throws IOException {

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return new ApiResponse<>("Book not found", null);

        String uploadDir = "uploads/book-covers/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = bookId + "_" + System.currentTimeMillis() + "_" + original;
        File dest = Paths.get(uploadDir, filename).toFile();
        file.transferTo(dest);

        // serve as http://localhost:8080/uploads/book-covers/...
        String imageUrl = "http://localhost:8080/uploads/book-covers/" + filename;
        book.setCoverImageUrl(imageUrl);
        bookRepository.save(book);

        return new ApiResponse<>("Cover uploaded successfully", imageUrl);
    }
}
