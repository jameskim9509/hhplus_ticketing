package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
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
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
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
    private WaitingQueueWriter waitingQueueWriter;
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
    @Mock
    private ReservationEventPublisher reservationEventPublisher;

    @InjectMocks
    private ReservationApplication reservationApplication;

    @Test
    void reserveSeat() {
        // given
        UserContext.setContext(
                User.builder().uuid(UUID.randomUUID().toString()).build()
        );

        Mockito.doReturn(Concert.builder().id(1L).build())
                .when(concertReader).getByDate(Mockito.any());
        Mockito.doReturn(
                Seat.builder()
                        .status(SeatStatus.AVAILABLE)
                        .build()
        ).when(seatReader).readAvailableSeatByConcertIdAndNumberWithLock(Mockito.anyLong(), Mockito.anyLong());

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        ArgumentCaptor<Seat> seatCaptor = ArgumentCaptor.forClass(Seat.class);
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);

        // when
        reservationApplication.reserveSeat(LocalDate.now(), 10L);
        Mockito.verify(reservationWriter).writeReservation(reservationCaptor.capture());
        Mockito.verify(seatModifier, Mockito.times(1)).setReserved(Mockito.any());
        Mockito.verify(waitingQueueModifier).changeExpiredTime(tokenCaptor.capture(), Mockito.anyDouble());

        Reservation capturedReservation = reservationCaptor.getValue();
        String capturedToken = tokenCaptor.getValue();
        // then
        Assertions.assertThat(capturedReservation.getConcert()).isNotNull();
        Assertions.assertThat(capturedReservation.getUser()).isNotNull();
        Assertions.assertThat(capturedReservation.getSeat()).isNotNull();
        Assertions.assertThat(capturedToken).isNotNull();
    }

    @Test
    void 요청자가_요청하지_않은_예약_조회시_에러()
    {
        // given
        UserContext.setContext(
                User.builder().id(1L).uuid(UUID.randomUUID().toString()).build()
        );

        Mockito.doReturn(
                Reservation.builder()
                        .user(User.builder().id(2L).build())
                        .build()
        ).when(reservationReader).readById(Mockito.anyLong());

        // when
        Assertions.assertThatThrownBy(
                        () -> reservationApplication.getReservation(1L)
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("요청자의 예약 정보가 아닙니다.");
    }
}