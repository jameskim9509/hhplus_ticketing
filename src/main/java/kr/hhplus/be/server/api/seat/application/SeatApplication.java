package kr.hhplus.be.server.api.seat.application;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.components.ReservationModifier;
import kr.hhplus.be.server.domain.reservation.components.ReservationReader;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.components.SeatModifier;
import kr.hhplus.be.server.domain.seat.components.SeatReader;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserReader;
import kr.hhplus.be.server.api.seat.dto.GetAvailableSeatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class SeatApplication implements SeatUsecase{
    private final UserReader userReader;
    private final WaitingQueueReader waitingQueueReader;
    private final ConcertReader concertReader;
    private final SeatReader seatReader;
    private final SeatModifier seatModifier;
    private final ReservationReader reservationReader;
    private final ReservationModifier reservationModifier;

    @Override
    @Transactional
    public List<Long> getAvailableSeatsByDate(LocalDate date)
    {
        List<Long> unavailableSeatNumbers = seatReader.getUnavailableSeatsByDate(date);
        return LongStream.rangeClosed(1, 50).filter(
                seatNumber -> !unavailableSeatNumbers.contains(seatNumber)
        ).boxed().toList();
    }

    @Transactional
    @Scheduled(cron = "0 */1 * * * *")
    public void updateSeat()
    {
        List<Reservation> reservationList = reservationReader.readAllPaymentRequiredWithLock();
        reservationList.stream()
                .filter(r -> r.getExpiredAt().isBefore(LocalDateTime.now()))
                .forEach(r ->
                {
                    seatModifier.setAvailable(r.getSeat());
                    r.setStatus(ReservationStatus.EXPIRED);
                    reservationModifier.modifyReservation(r);
                });
    }
}
