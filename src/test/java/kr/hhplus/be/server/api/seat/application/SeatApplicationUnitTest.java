package kr.hhplus.be.server.api.seat.application;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.components.ReservationModifier;
import kr.hhplus.be.server.domain.reservation.components.ReservationReader;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.components.UserReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SeatApplicationUnitTest {
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private ReservationReader reservationReader;
    @Mock
    private ReservationModifier reservationModifier;
    @Mock
    private UserReader userReader;

    @InjectMocks
    private SeatApplication seatApplication;

    @Test
    void updateSeat() {
        // given
        Mockito.doReturn(
                List.of(
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .seat(Seat.builder().build())
                                .build(),
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .seat(Seat.builder().build())
                                .build(),
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .seat(Seat.builder().build())
                                .build()
                )
        ).when(reservationReader).readAllPaymentRequiredWithLock();

        // when
        seatApplication.updateSeat();

        // then
        Mockito.verify(reservationModifier, Mockito.times(2))
                .modifyReservation(Mockito.any());;
    }

    @Test
    void 활성화되지_않은_토큰으로_좌석_조회시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidToken(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> seatApplication.getAvailableSeatsByDate(
                                LocalDate.now(),
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }
}