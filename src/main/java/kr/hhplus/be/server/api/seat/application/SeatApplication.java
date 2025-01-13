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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatApplication implements SeatUsecase{
    private final UserReader userReader;
    private final WaitingQueueReader waitingQueueReader;
    private final ConcertReader concertReader;
    private final SeatReader seatReader;
    private final ReservationReader reservationReader;
    private final ReservationModifier reservationModifier;

    @Override
    @Transactional
    public List<Seat> getAvailableSeatsByDate(LocalDate date, String uuid)
    {
        User user = userReader.readByUuid(uuid);
        if (waitingQueueReader.readValidToken(user).getStatus() != WaitingQueueStatus.ACTIVE)
            throw new ConcertException(ErrorCode.TOKEN_IS_INVALID);

        return seatReader.getAvailableSeats(concertReader.getByDate(date));
    }

    @Transactional
    @Scheduled(cron = "")
    public void updateSeat()
    {
        List<Reservation> reservationList = reservationReader.readAllPaymentRequiredWithLock();
        reservationList.stream()
                .filter(r -> r.getExpiredAt().isBefore(LocalDateTime.now()))
                .forEach(r ->
                {
                    r.getSeat().setStatus(SeatStatus.AVAILABLE);
                    r.setStatus(ReservationStatus.EXPIRED);
                    reservationModifier.modifyReservation(r);
                });
    }
}
