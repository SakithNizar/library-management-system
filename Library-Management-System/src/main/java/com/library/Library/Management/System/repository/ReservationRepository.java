package com.library.Library.Management.System.repository;

import com.library.Library.Management.System.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
