package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.domain.reservation.Reservation;

import java.time.LocalDate;

public interface ReservationUsecase {
    public Reservation getReservation(Long reservationId, String uuid);
    public Reservation reserveSeat(LocalDate date, Long seatNumber, String uuid);
}
