package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.*;
import com.library.Library.Management.System.repository.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final CategoryRepository categoryRepo;
    private final ReservationRepository resRepo;

    public TestController(UserRepository userRepo, BookRepository bookRepo, CategoryRepository categoryRepo, ReservationRepository resRepo) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.categoryRepo = categoryRepo;
        this.resRepo = resRepo;
    }

    @GetMapping("/seed")
    public String seedData() {
        Category tech = new Category("Techy");
        categoryRepo.save(tech);

        Book book = new Book("Clean Code", "Robert C. Martin", "Programming", "English", "9780132350884", tech);
        bookRepo.save(book);

        User user = new User("testuser@library.com", "pass123",Role.USER);
        userRepo.save(user);

        Reservation res = new Reservation(user, book, LocalDate.now(), LocalDate.now().plusDays(14));
        resRepo.save(res);

        return "✅ Data seeded successfully!";
    }
}