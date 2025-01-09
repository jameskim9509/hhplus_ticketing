package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationWriterRepositoryImpl implements ReservationWriterRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation writeReservation(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }
}
