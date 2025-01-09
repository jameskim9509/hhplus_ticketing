package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatReaderRepositoryImpl implements SeatReaderRepository {
    private final SeatJpaRepository seatJpaRepository;

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
