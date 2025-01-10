package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeatModifierRepositoryImpl implements SeatModifierRepository{
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Seat modifySeat(Seat seat) {
        return seatJpaRepository.save(seat);
    }
}
