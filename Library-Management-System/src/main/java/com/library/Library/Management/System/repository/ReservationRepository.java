package com.library.Library.Management.System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.Library.Management.System.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
