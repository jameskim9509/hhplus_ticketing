package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.dto.ReservationSuccessEvent;
import kr.hhplus.be.server.api.token.application.TokenApplication;
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
import java.time.ZoneOffset;

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

    private final ReservationEventPublisher reservationEventPublisher;

    private static final Long RESERVATION_LIFETIME_IN_MINUTES = 5L;

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

        Concert concert = concertReader.getByDate(date);
        // lock을 걸어 가져와야 하기 때문에 concert.getSeatList() (x)
        Seat seat = seatReader.readAvailableSeatByConcertIdAndNumberWithLock(concert.getId(), seatNumber);

        seatModifier.setReserved(seat);

        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(RESERVATION_LIFETIME_IN_MINUTES);
        Reservation reservation = Reservation.builder()
                .expiredAt(expiredTime)
                .status(ReservationStatus.PAYMENT_REQUIRED)
                .user(user)
                .build();
        reservation.setConcert(concert);
        reservation.setSeat(seat);

        reservationWriter.writeReservation(reservation);

        // 레디스 명령어를 제일 마지막에 수행함으로써 레디스 트랜잭션 사용 X
        // 토큰 값 덮어쓰기
        // 스케줄러가 만료시간이 다 되어 토큰을 지워버렸다하더라도, 새로운 토큰을 생성해서 집어넣는다.
        waitingQueueModifier.changeExpiredTime(
                user.getUuid(),
                expiredTime.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000 + expiredTime.getNano()
        );

        reservationEventPublisher.success(new ReservationSuccessEvent());

        return reservation;
    }
}
