package kr.hhplus.be.server.domain.reservation.repositories;

import kr.hhplus.be.server.domain.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationReaderRepository {
    public Optional<Reservation> readByIdWithLock(Long reservationId);
    public List<Reservation> readAllPaymentRequiredWithLock();
}
