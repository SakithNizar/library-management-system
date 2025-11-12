package com.library.Library.Management.System.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each reservation belongs to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Each reservation belongs to one book
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Reservation details
    @Column(name = "reservation_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status; // e.g., ACTIVE, RETURNED, CANCELLED

    // Constructors
    public Reservation() {}

    public Reservation(User user, Book book, LocalDate startDate, LocalDate endDate, String status) {
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Constructor with default ACTIVE status
    public Reservation(User user, Book book, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
