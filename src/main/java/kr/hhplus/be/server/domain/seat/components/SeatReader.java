package kr.hhplus.be.server.domain.seat.components;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatReaderRepository;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatReader {
    private final SeatReaderRepository seatReaderRepository;

    public List<Seat> getAvailableSeats(Concert concert)
    {
        return concert.getSeatList().stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE).toList();
    }

    public Seat readAvailableSeatByConcertIdAndNumberWithLock(Long concertId, Long seatNumber)
    {
        return seatReaderRepository.getSeatByConcertIdAndNumberWithLock(concertId, seatNumber).get();
    }

    public Seat getById(Long seatId)
    {
        return seatReaderRepository.getById(seatId).get();
    }
}
