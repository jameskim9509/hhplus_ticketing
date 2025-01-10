package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationReaderRepository;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationReaderRepositoryImpl implements ReservationReaderRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<Reservation> readById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }

    @Override
    public Optional<Reservation> readByIdWithLock(Long reservationId) {
        return reservationJpaRepository.findByIdWithLock(reservationId);
    }

    @Override
    public List<Reservation> readAllPaymentRequiredWithLock() {
        return reservationJpaRepository.readAllByStatusWithLock(ReservationStatus.PAYMENT_REQUIRED);
    }
}
