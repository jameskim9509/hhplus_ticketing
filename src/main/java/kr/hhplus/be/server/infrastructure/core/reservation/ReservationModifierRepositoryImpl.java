package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationModifierRepositoryImpl implements ReservationModifierRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation modifyReservation(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }
}
