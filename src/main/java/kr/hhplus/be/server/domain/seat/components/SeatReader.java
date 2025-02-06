package kr.hhplus.be.server.domain.seat.components;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatReaderRepository;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatReader {
    private final SeatReaderRepository seatReaderRepository;

    public List<Seat> getAvailableSeats(Concert concert)
    {
        List<Seat> seatList = concert.getSeatList().stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE).toList();
        if (seatList.isEmpty())
            throw new ConcertException(ErrorCode.AVAILABLE_SEAT_NOT_FOUND);

        return seatList;
    }

    public List<Long> getUnavailableSeatsByDate(LocalDate date)
    {
        return seatReaderRepository.getUnavailableSeatsByDate(date);
    }

    public Seat readAvailableSeatByConcertIdAndNumberWithLock(Long concertId, Long seatNumber)
    {
        Seat seat =  seatReaderRepository.getSeatByConcertIdAndNumberWithLock(concertId, seatNumber)
                .orElseThrow(() -> new ConcertException(ErrorCode.SEAT_NOT_FOUND));
        if (seat.getStatus() != SeatStatus.AVAILABLE)
            throw new ConcertException(ErrorCode.SEAT_IS_INVALID);

        return seat;
    }

    public Seat getById(Long seatId)
    {
        return seatReaderRepository.getById(seatId)
                .orElseThrow(() -> new ConcertException(ErrorCode.SEAT_NOT_FOUND));
    }
}
