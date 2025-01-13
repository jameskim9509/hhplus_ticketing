package kr.hhplus.be.server.domain.reservation.components;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationReader {
    private final ReservationReaderRepository reservationReaderRepository;

    public Reservation readById(Long reservationId)
    {
        return reservationReaderRepository.readById(reservationId)
                .orElseThrow(() -> new ConcertException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public Reservation readByIdWithLock(Long reservationId)
    {
        return reservationReaderRepository.readByIdWithLock(reservationId)
                .orElseThrow(() -> new ConcertException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public List<Reservation> readAllPaymentRequiredWithLock()
    {
        return reservationReaderRepository.readAllPaymentRequiredWithLock();
    }
}
