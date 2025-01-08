package kr.hhplus.be.server.domain.reservation.repositories;

import kr.hhplus.be.server.domain.reservation.Reservation;

public interface ReservationModifierRepository {
    public Reservation modifyReservation(Reservation reservation);
}
