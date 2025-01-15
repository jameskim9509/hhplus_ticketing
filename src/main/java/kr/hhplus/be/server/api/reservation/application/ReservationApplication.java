package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.components.ReservationReader;
import kr.hhplus.be.server.domain.reservation.components.ReservationWriter;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.components.SeatModifier;
import kr.hhplus.be.server.domain.seat.components.SeatReader;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueModifier;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationApplication implements ReservationUsecase{
    private final UserReader userReader;
    private final ReservationReader reservationReader;
    private final WaitingQueueWriter waitingQueueWriter;
    private final WaitingQueueReader waitingQueueReader;
    private final ConcertReader concertReader;
    private final SeatReader seatReader;
    private final ReservationWriter reservationWriter;
    private final SeatModifier seatModifier;
    private final WaitingQueueModifier waitingQueueModifier;

    @Override
    @Transactional
    public Reservation getReservation(Long reservationId)
    {
        Reservation reservation = reservationReader.readById(reservationId);

        if (UserContext.getContext().getId().longValue() != reservation.getUser().getId().longValue())
            throw new ConcertException(ErrorCode.RESERVATION_NOT_MATCHED);

        return reservation;
    }

    @Override
    @Transactional
    public Reservation reserveSeat(LocalDate date, Long seatNumber)
    {
        User user = UserContext.getContext();
        WaitingQueue token = waitingQueueReader.readValidToken(user);

        Concert concert = concertReader.getByDate(date);
        // lock을 걸어 가져와야 하기 때문에 concert.getSeatList() (x)
        Seat seat = seatReader.readAvailableSeatByConcertIdAndNumberWithLock(concert.getId(), seatNumber);

        seat.setStatus(SeatStatus.RESERVED);

        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(5);
        Reservation reservation = Reservation.builder()
                .expiredAt(expiredTime)
                .status(ReservationStatus.PAYMENT_REQUIRED)
                .user(user)
                .build();
        reservation.setConcert(concert);
        reservation.setSeat(seat);

        // 기존 토큰을 만료하고, 만료시간이 추가된 새 토큰을 생성하여 lock 사용 x
        token.expire(LocalDateTime.now());
        waitingQueueWriter.writeToken(
                WaitingQueue.builder()
                        .user(user)
                        .status(WaitingQueueStatus.ACTIVE)
                        .expiredAt(expiredTime)
                        .build()
        );

        reservationWriter.writeReservation(reservation);
        seatModifier.modifySeat(seat);
        waitingQueueModifier.modifyToken(token);

        return reservation;
    }
}
