package kr.hhplus.be.server.domain.reservation.components;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationReader {
    private final ReservationReaderRepository reservationReaderRepository;

    public Reservation readByIdWithLock(Long reservationId)
    {
        return reservationReaderRepository.readByIdWithLock(reservationId).get();
    }

    public List<Reservation> readAllPaymentRequiredWithLock()
    {
        return reservationReaderRepository.readAllPaymentRequiredWithLock();
    }
}
