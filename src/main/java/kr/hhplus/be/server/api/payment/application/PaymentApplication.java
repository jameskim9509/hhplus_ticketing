package kr.hhplus.be.server.api.payment.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.components.PaymentWriter;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.components.ReservationModifier;
import kr.hhplus.be.server.domain.reservation.components.ReservationReader;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueModifier;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentApplication implements PaymentUsecase{
    private final UserReader userReader;
    private final WaitingQueueReader waitingQueueReader;
    private final WaitingQueueModifier waitingQueueModifier;
    private final UserModifier userModifier;
    private final ReservationModifier reservationModifier;
    private final ReservationReader reservationReader;
    private final PaymentWriter paymentWriter;

    @Override
    @Transactional
    public Payment pay(Long reservationId)
    {
        User user = UserContext.getContext();
        WaitingQueue token = waitingQueueReader.readValidToken(user);

        Reservation reservation = reservationReader.readByIdWithLock(reservationId);
        if (reservation.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new ConcertException(ErrorCode.RESERVATION_EXPIRED);
        if (reservation.getStatus() != ReservationStatus.PAYMENT_REQUIRED)
            throw new ConcertException(ErrorCode.RESERVATION_NOT_PAYMENT_REQUIRED);
        if (user.getId().longValue() != reservation.getUser().getId().longValue())
            throw new ConcertException(ErrorCode.RESERVATION_NOT_MATCHED);

        Long seatCost = reservation.getSeatCost();
        user.usePoint(seatCost);

        LocalDateTime now = LocalDateTime.now();
        token.expire(now);
        reservation.confirm(now);

        waitingQueueModifier.modifyToken(token);
        userModifier.modifyUser(user);
        reservationModifier.modifyReservation(reservation);

        Payment payment = Payment.builder()
                .createdAt(now)
                .point(seatCost)
                .user(user)
                .reservation(reservation)
                .build();

        paymentWriter.createPayment(payment);

        return payment;
    }
}
