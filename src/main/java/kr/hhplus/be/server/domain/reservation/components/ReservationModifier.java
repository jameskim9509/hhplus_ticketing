package kr.hhplus.be.server.domain.reservation.components;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.repositories.ReservationModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationModifier {
    private final ReservationModifierRepository reservationModifierRepository;

    public Reservation modifyReservation(Reservation reservation)
    {
        return reservationModifierRepository.modifyReservation(reservation);
    }
}
