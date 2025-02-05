package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatReaderRepository;
import kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository.UNAVAILABLE_SEAT_SET_PREFIX;

@Repository
@RequiredArgsConstructor
public class SeatReaderRepositoryImpl implements SeatReaderRepository {
    private final SeatJpaRepository seatJpaRepository;
    private final SeatRedisRepository seatRedisRepository;

    @Override
    public List<Long> getUnavailableSeatsByDate(LocalDate date) {
        return seatRedisRepository.SetMembers(UNAVAILABLE_SEAT_SET_PREFIX + date.toString())
                .stream()
                .map(Long::valueOf)
                .toList();
    }

    @Override
    public List<Seat> getSeatsByConcertId(Long concertId) {
        return seatJpaRepository.findAllByConcertId(concertId);
    }

    @Override
    public Optional<Seat> getSeatByConcertIdAndNumberWithLock(Long concertId, Long seatNumber) {
        return seatJpaRepository.findByConcertIdAndNumberWithLock(concertId, seatNumber);
    }

    @Override
    public Optional<Seat> getById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }
}
