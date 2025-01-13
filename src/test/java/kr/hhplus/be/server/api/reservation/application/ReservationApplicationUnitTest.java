package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.components.ReservationReader;
import kr.hhplus.be.server.domain.reservation.components.ReservationWriter;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ReservationApplicationUnitTest {
    @Mock
    private UserReader userReader;
    @Mock
    private ReservationReader reservationReader;
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private ConcertReader concertReader;
    @Mock
    private SeatReader seatReader;
    @Mock
    private ReservationWriter reservationWriter;
    @Mock
    private SeatModifier seatModifier;
    @Mock
    private WaitingQueueModifier waitingQueueModifier;

    @InjectMocks
    private ReservationApplication reservationApplication;

    @Test
    void reserveSeat() {
        // given
        Mockito.doReturn(User.builder().build())
                .when(userReader).readByUuid(Mockito.anyString());

        Mockito.doReturn(
                WaitingQueue.builder()
                        .status(WaitingQueueStatus.ACTIVE)
                        .build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());

        Mockito.doReturn(Concert.builder().id(1L).build())
                .when(concertReader).getByDate(Mockito.any());

        Mockito.doReturn(
                Seat.builder()
                        .status(SeatStatus.AVAILABLE)
                        .build()
        ).when(seatReader).readAvailableSeatByConcertIdAndNumberWithLock(Mockito.anyLong(), Mockito.anyLong());

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        ArgumentCaptor<Seat> seatCaptor = ArgumentCaptor.forClass(Seat.class);
        ArgumentCaptor<WaitingQueue> tokenCaptor = ArgumentCaptor.forClass(WaitingQueue.class);

        // when
        reservationApplication.reserveSeat(LocalDate.now(), 10L, UUID.randomUUID().toString());
        Mockito.verify(reservationWriter).writeReservation(reservationCaptor.capture());
        Mockito.verify(seatModifier).modifySeat(seatCaptor.capture());
        Mockito.verify(waitingQueueModifier).modifyToken(tokenCaptor.capture());

        Reservation capturedReservation = reservationCaptor.getValue();
        Seat capturedSeat = seatCaptor.getValue();
        WaitingQueue capturedToken = tokenCaptor.getValue();

        // then
        Assertions.assertThat(capturedReservation.getConcert()).isNotNull();
        Assertions.assertThat(capturedReservation.getUser()).isNotNull();
        Assertions.assertThat(capturedReservation.getSeat()).isNotNull();
        Assertions.assertThat(capturedSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
        Assertions.assertThat(capturedToken.getExpiredAt()).isNotNull();
    }

    @Test
    void 활성화되지_않은_토큰으로_예약시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> reservationApplication.reserveSeat(
                                LocalDate.now(),
                                30L,
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }

    @Test
    void 요청자가_요청하지_않은_예약_조회시_에러()
    {
        // given
        Mockito.doReturn(User.builder().id(1L).build())
                .when(userReader).readByUuid(Mockito.anyString());

        Mockito.doReturn(
                Reservation.builder()
                        .user(User.builder().id(2L).build())
                        .build()
        ).when(reservationReader).readById(Mockito.anyLong());

        // when
        Assertions.assertThatThrownBy(
                        () -> reservationApplication.getReservation(1L, UUID.randomUUID().toString())
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("요청자의 예약 정보가 아닙니다.");
    }
}