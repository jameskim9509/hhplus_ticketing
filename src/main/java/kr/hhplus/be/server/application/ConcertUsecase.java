package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.reservation.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.components.PaymentManager;
import kr.hhplus.be.server.domain.reservation.components.ReservationModifier;
import kr.hhplus.be.server.domain.reservation.components.ReservationReader;
import kr.hhplus.be.server.domain.reservation.components.ReservationWriter;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.components.SeatModifier;
import kr.hhplus.be.server.domain.seat.components.SeatReader;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.components.WaitingQueueModifier;
import kr.hhplus.be.server.domain.waiting_queue.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.waiting_queue.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
import kr.hhplus.be.server.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertUsecase {
    private final ConcertReader concertReader;
    private final SeatReader seatReader;
    private final SeatModifier seatModifier;
    private final ReservationWriter reservationWriter;
    private final ReservationReader reservationReader;
    private final ReservationModifier reservationModifier;
    private final PaymentManager paymentManager;
    private final UserModifier userModifier;
    private final UserReader userReader;
    private final WaitingQueueReader waitingQueueReader;
    private final WaitingQueueModifier waitingQueueModifier;
    private final WaitingQueueWriter waitingQueueWriter;

    public static final Integer MAX_ACTIVE_USER = 10;

    public CreateTokenResponse createToken(Long userId)
    {
        User user = userReader.readById(userId);
        String uuid = user.getUuid();
        if (Objects.isNull(uuid)) {
            uuid = UUID.randomUUID().toString();
            userModifier.modifyUser(
                    User.builder()
                            .id(user.getId())
                            .balance(user.getBalance())
                            .uuid(uuid)
                            .build()
            );
        }

        if (waitingQueueReader.isValidTokenExists(user))
        {
            throw new RuntimeException("토큰이 이미 존재합니다.");
        }

        List<WaitingQueue> tokenList = waitingQueueReader.readAllActiveTokens();

        WaitingQueue token = WaitingQueue.builder()
                .user(user)
                .build();
        if (tokenList.size() < MAX_ACTIVE_USER) {
            token.setStatus(WaitingQueueStatus.ACTIVE);
            token.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        }
        else
            token.setStatus(WaitingQueueStatus.WAIT);

        waitingQueueWriter.writeToken(token);

        return new CreateTokenResponse(uuid);
    }

    public GetTokenResponse getToken(String uuid)
    {
        User user = userReader.readByUuid(uuid);
        WaitingQueue token = waitingQueueReader.readValidToken(user);

        if (token.getStatus() == WaitingQueueStatus.ACTIVE)
            return new GetTokenResponse(0L);

        WaitingQueue activeToken = waitingQueueReader.readActiveTokenWithMaxId();

        return new GetTokenResponse(token.getId() - activeToken.getId());
    }

    @Scheduled(cron = "")
    public void updateWaitingQueue()
    {
        List<WaitingQueue> tokenList = waitingQueueReader.readAllActiveTokensWithLock();
        tokenList.stream()
                .filter(t -> t.getExpiredAt().isBefore(LocalDateTime.now()))
                .forEach(t -> t.setStatus(WaitingQueueStatus.EXPIRED));

        List<WaitingQueue> activeTokenList = tokenList.stream()
                .filter(t -> t.getStatus() == WaitingQueueStatus.ACTIVE)
                .toList();
        if (activeTokenList.size() < MAX_ACTIVE_USER)
        {
            waitingQueueReader.readWaitTokensLimitBy(PageRequest.of(0, MAX_ACTIVE_USER - activeTokenList.size())).stream()
                    .forEach(t ->
                    {
                        t.setStatus(WaitingQueueStatus.ACTIVE);
                        t.setExpiredAt(LocalDateTime.now().plusMinutes(10));
                        waitingQueueModifier.modifyToken(t);
                    });
        }
    }

    @Scheduled(cron = "")
    public void deleteExpiredToken()
    {
        waitingQueueModifier.deleteAllTokens(waitingQueueReader.readAllExpiredTokens());
    }

    public GetAvailableConcertsResponse getAvailableConcerts(LocalDate startDate, LocalDate endDate, String uuid)
    {
        User user = userReader.readByUuid(uuid);
        if (waitingQueueReader.readValidToken(user).getStatus() != WaitingQueueStatus.ACTIVE)
            throw new RuntimeException("활성화되지 않은 토큰입니다.");

        List<Concert> concertList = concertReader.readByDateBetween(startDate, endDate);
        return new GetAvailableConcertsResponse(concertList);
    }

    public GetAvailableSeatsResponse getAvailableSeatsByDate(LocalDate date, String uuid)
    {
        User user = userReader.readByUuid(uuid);
        if (waitingQueueReader.readValidToken(user).getStatus() != WaitingQueueStatus.ACTIVE)
            throw new RuntimeException("활성화되지 않은 토큰입니다.");

        Concert concert = concertReader.getByDate(date);
        return new GetAvailableSeatsResponse(
                seatReader.getAvailableSeats(concert)
        );
    }

    public ReserveSeatResponse reserveSeat(LocalDate date, Long seatNumber, String uuid)
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

        return new ReserveSeatResponse(reservation);
    }

    public ReserveSeatResponse pay(Long reservationId, String uuid)
    {
        User user = userReader.readByUuidWithLock(uuid);
        WaitingQueue token = waitingQueueReader.readValidTokenByUuidWithLock(uuid);
        if (token.getStatus() != WaitingQueueStatus.ACTIVE)
            throw new RuntimeException("활성화되지 않은 토큰입니다.");

        Reservation reservation = reservationReader.readByIdWithLock(reservationId);
        if (reservation.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("만료된 예약입니다.");
        if (reservation.getStatus() != ReservationStatus.PAYMENT_REQUIRED)
            throw new RuntimeException("결제가 필요한 예약이 아닙니다.");
        if (user.getId() != reservation.getUser().getId())
            throw new RuntimeException("요청자의 예약 정보가 아닙니다.");

        Long seatCost = reservation.getSeatCost();
        user.usePoint(seatCost);

        LocalDateTime now = LocalDateTime.now();
        token.expire(now);
        reservation.confirm(now);

        waitingQueueModifier.modifyToken(token);
        userModifier.modifyUser(user);
        reservationModifier.modifyReservation(reservation);
        paymentManager.createPayment(
                        Payment.builder()
                                .createdAt(now)
                                .point(seatCost)
                                .user(user)
                                .reservation(reservation)
                                .build()
        );

        return new ReserveSeatResponse(reservation);
    }

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

    public ChargePointResponse chargePoint(Long point, String uuid)
    {
        User user = userReader.readByUuidWithLock(uuid);
        user.chargePoint(point);
        userModifier.modifyUser(user);
        return new ChargePointResponse(user.getId(), user.getBalance());
    }
}
