package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.dto.ReservationRequest;
import com.library.Library.Management.System.dto.ReservationResponse;
import com.library.Library.Management.System.model.*;
import com.library.Library.Management.System.repository.*;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationController(
            ReservationRepository reservationRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // -----------------------------------------------------
    // 1️⃣ USER — Create Reservation (NO userId needed)
    // -----------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ApiResponse<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            Authentication authentication
    ) {
        // Logged-in user email from JWT
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        // Find Book
        Book book = bookRepository.findById(request.getBookId()).orElse(null);
        if (book == null) {
            return new ApiResponse<>("Book not found!", null);
        }

        // Create reservation
        Reservation reservation = new Reservation(
                user,
                book,
                LocalDate.parse(request.getStartDate()),
                LocalDate.parse(request.getEndDate()),
                ReservationStatus.ACTIVE
        );

        Reservation saved = reservationRepository.save(reservation);

        // Build clean response
        ReservationResponse response = new ReservationResponse(
                saved.getId(),
                user.getEmail(),
                book.getTitle(),
                saved.getStartDate().toString(),
                saved.getEndDate().toString(),
                saved.getStatus().name()
        );

        return new ApiResponse<>("Reservation created successfully!", response);
    }

    // -----------------------------------------------------
    // 2️⃣ LIBRARIAN — View All Reservations
    // -----------------------------------------------------
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<List<ReservationResponse>> getAllReservations() {

        List<ReservationResponse> list = reservationRepository.findAll()
                .stream()
                .map(res -> new ReservationResponse(
                        res.getId(),
                        res.getUser().getEmail(),
                        res.getBook().getTitle(),
                        res.getStartDate().toString(),
                        res.getEndDate().toString(),
                        res.getStatus().name()
                ))
                .toList();

        return new ApiResponse<>("Fetched all reservations successfully!", list);
    }

    // -----------------------------------------------------
    // 3️⃣ USER + LIBRARIAN — View by ID
    // -----------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<ReservationResponse> getReservationById(@PathVariable Long id) {

        return reservationRepository.findById(id)
                .map(res -> new ApiResponse<>("Reservation found!",
                        new ReservationResponse(
                                res.getId(),
                                res.getUser().getEmail(),
                                res.getBook().getTitle(),
                                res.getStartDate().toString(),
                                res.getEndDate().toString(),
                                res.getStatus().name()
                        )
                ))
                .orElseGet(() -> new ApiResponse<>("Reservation not found!", null));
    }

    // ✅ UPDATE reservation (USER + LIBRARIAN)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequest request,
            Authentication auth
    ) {
        // Get logged-in user email
        String email = auth.getName();
        User loggedUser = userRepository.findByEmail(email);

        Reservation reservation = reservationRepository.findById(id).orElse(null);

        if (reservation == null) {
            return new ApiResponse<>("Reservation not found!", null);
        }

        // USER can only update their own reservation
        if (loggedUser.getRole() == Role.USER &&
                !reservation.getUser().getEmail().equals(email)) {

            return new ApiResponse<>("You are not allowed to update this reservation!", null);
        }

        // Update values
        if (request.getStartDate() != null)
            reservation.setStartDate(LocalDate.parse(request.getStartDate()));

        if (request.getEndDate() != null)
            reservation.setEndDate(LocalDate.parse(request.getEndDate()));

        if (request.getStatus() != null)
            reservation.setStatus(ReservationStatus.valueOf(request.getStatus()));

        Reservation saved = reservationRepository.save(reservation);

        ReservationResponse response = new ReservationResponse(
                saved.getId(),
                saved.getUser().getEmail(),
                saved.getBook().getTitle(),
                saved.getStartDate().toString(),
                saved.getEndDate().toString(),
                saved.getStatus().toString()
        );

        return new ApiResponse<>("Reservation updated successfully!", response);
    }


    // -----------------------------------------------------
    // 4️⃣ USER + LIBRARIAN — Cancel Reservation
    // -----------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<String> cancelReservation(@PathVariable Long id) {

        Reservation res = reservationRepository.findById(id).orElse(null);

        if (res == null) {
            return new ApiResponse<>("Reservation not found!", null);
        }

        // Soft delete = CANCELLED
        res.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(res);

        return new ApiResponse<>("Reservation cancelled!", "Cancelled ID: " + id);
    }
}
