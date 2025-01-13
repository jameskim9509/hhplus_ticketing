package kr.hhplus.be.server.api.reservation.application;

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
    private final WaitingQueueReader waitingQueueReader;
    private final ConcertReader concertReader;
    private final SeatReader seatReader;
    private final ReservationWriter reservationWriter;
    private final SeatModifier seatModifier;
    private final WaitingQueueModifier waitingQueueModifier;

    @Override
    @Transactional
    public Reservation getReservation(Long reservationId, String uuid)
    {
        User user = userReader.readByUuid(uuid);
        Reservation reservation = reservationReader.readById(reservationId);

        if (user.getId() != reservation.getUser().getId())
            throw new RuntimeException("요청자의 예약 정보가 아닙니다.");

        return reservation;
    }

    @Override
    @Transactional
    public Reservation reserveSeat(LocalDate date, Long seatNumber, String uuid)
    {
        User user = userReader.readByUuid(uuid);
        WaitingQueue token = waitingQueueReader.readValidTokenByUuidWithLock(uuid);
        if (token.getStatus() != WaitingQueueStatus.ACTIVE)
            throw new RuntimeException("활성화되지 않은 토큰입니다.");

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

        token.setExpiredAt(expiredTime);

        reservationWriter.writeReservation(reservation);
        seatModifier.modifySeat(seat);
        waitingQueueModifier.modifyToken(token);

        return reservation;
    }
}
