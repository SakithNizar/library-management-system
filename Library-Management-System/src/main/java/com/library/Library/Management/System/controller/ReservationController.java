package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Reservation;
import com.library.Library.Management.System.repository.ReservationRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // LIBRARIAN — View all reservations
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<List<Reservation>> getAllReservations() {
        return new ApiResponse<>("Fetched all reservations successfully!", reservationRepository.findAll());
    }

    // USER + LIBRARIAN — view by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(res -> new ApiResponse<>("Reservation found!", res))
                .orElseGet(() -> new ApiResponse<>("Reservation not found!", null));
    }

    // USER — Create reservation
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ApiResponse<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation saved = reservationRepository.save(reservation);
        return new ApiResponse<>("Reservation created successfully!", saved);
    }

    // USER + LIBRARIAN — update reservation
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation updated) {
        return reservationRepository.findById(id)
                .map(res -> {
                    res.setStartDate(updated.getStartDate());
                    res.setEndDate(updated.getEndDate());
                    res.setStatus(updated.getStatus());
                    Reservation saved = reservationRepository.save(res);
                    return new ApiResponse<>("Reservation updated successfully!", saved);
                })
                .orElseGet(() -> new ApiResponse<>("Reservation not found!", null));
    }

    // USER + LIBRARIAN — delete reservation
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<String> deleteReservation(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            return new ApiResponse<>("Reservation not found!", null);
        }

        reservationRepository.deleteById(id);
        return new ApiResponse<>("Reservation deleted successfully!", "Deleted ID: " + id);
    }
}

