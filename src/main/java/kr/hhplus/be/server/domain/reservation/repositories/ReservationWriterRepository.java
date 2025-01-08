package kr.hhplus.be.server.domain.reservation.repositories;

import kr.hhplus.be.server.domain.reservation.Reservation;

public interface ReservationWriterRepository {
    public Reservation writeReservation(Reservation reservation);
}
