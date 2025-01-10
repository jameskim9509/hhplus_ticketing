package kr.hhplus.be.server.domain.reservation.components;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationWriter {
    private final ReservationWriterRepository reservationWriterRepository;

    public Reservation writeReservation(Reservation reservation)
    {
        return reservationWriterRepository.writeReservation(reservation);
    }
}
