package kr.hhplus.be.server.domain.seat.repositories;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatReaderRepository {
    public List<Seat> getSeatsByConcertId(Long concertId);
    public Optional<Seat> getSeatByConcertIdAndNumberWithLock(Long concertId, Long seatNumber);
    public Optional<Seat> getById(Long seatId);
}
