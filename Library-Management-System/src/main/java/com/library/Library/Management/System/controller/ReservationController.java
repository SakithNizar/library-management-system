package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Reservation;
import com.library.Library.Management.System.repository.ReservationRepository;
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

    // ✅ GET all reservations
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // ✅ GET reservation by ID
    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    // ✅ POST create new reservation
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // ✅ PUT update reservation (e.g., status or endDate)
    @PutMapping("/{id}")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody Reservation updatedReservation) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setStartDate(updatedReservation.getStartDate());
                    reservation.setEndDate(updatedReservation.getEndDate());
                    reservation.setStatus(updatedReservation.getStatus());
                    return reservationRepository.save(reservation);
                })
                .orElse(null);
    }

    // ✅ DELETE reservation
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
    }
}
