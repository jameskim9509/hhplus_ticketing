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
        List<Seat> seatList = concert.getSeatList().stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE).toList();
        if (seatList.isEmpty())
            throw new RuntimeException("이용가능한 좌석이 없습니다.");

        return seatList;
    }

    public Seat readAvailableSeatByConcertIdAndNumberWithLock(Long concertId, Long seatNumber)
    {
        Seat seat =  seatReaderRepository.getSeatByConcertIdAndNumberWithLock(concertId, seatNumber)
                .orElseThrow(() -> new RuntimeException("없는 좌석 입니다."));
        if (seat.getStatus() != SeatStatus.AVAILABLE)
            throw new RuntimeException("사용 가능한 좌석이 아닙니다.");

        return seat;
    }

    public Seat getById(Long seatId)
    {
        return seatReaderRepository.getById(seatId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 좌석입니다."));
    }
}
