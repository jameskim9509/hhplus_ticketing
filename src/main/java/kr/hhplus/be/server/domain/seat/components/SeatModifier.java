package kr.hhplus.be.server.domain.seat.components;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SeatModifier {
    private final SeatModifierRepository seatModifierRepository;

    public Seat modifySeat(Seat seat)
    {
        return seatModifierRepository.modifySeat(seat);
    }
}
