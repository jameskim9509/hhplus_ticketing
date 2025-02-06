package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatModifierRepository;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository.UNAVAILABLE_SEAT_SET_PREFIX;

@Repository
@RequiredArgsConstructor
public class SeatModifierRepositoryImpl implements SeatModifierRepository{
    private final SeatJpaRepository seatJpaRepository;
    private final SeatRedisRepository seatRedisRepository;

    @Override
    public Seat modifySeat(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    @Override
    public void setReserved(Seat seat) {
        seat.setStatus(SeatStatus.RESERVED);
        seatJpaRepository.save(seat);
        seatRedisRepository.SetAdd(
                UNAVAILABLE_SEAT_SET_PREFIX + seat.getConcert().getDate(),
                seat.getNumber().toString()
        );
    }

    @Override
    public void setAvailable(Seat seat) {
        seat.setStatus(SeatStatus.AVAILABLE);
        seatJpaRepository.save(seat);
        seatRedisRepository.SetRemove(
                UNAVAILABLE_SEAT_SET_PREFIX + seat.getConcert().getDate(),
                seat.getNumber().toString()
        );
    }
}
