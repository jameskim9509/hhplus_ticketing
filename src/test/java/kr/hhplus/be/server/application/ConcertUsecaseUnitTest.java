package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
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
import kr.hhplus.be.server.dto.GetTokenResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ConcertUsecaseUnitTest {
    @Mock
    private ConcertReader concertReader;
    @Mock
    private SeatReader seatReader;
    @Mock
    private SeatModifier seatModifier;
    @Mock
    private ReservationWriter reservationWriter;
    @Mock
    private ReservationReader reservationReader;
    @Mock
    private ReservationModifier reservationModifier;
    @Mock
    private PaymentManager paymentManager;
    @Mock
    private UserModifier userModifier;
    @Mock
    private UserReader userReader;
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private WaitingQueueModifier waitingQueueModifier;
    @Mock
    private WaitingQueueWriter waitingQueueWriter;
    @InjectMocks
    private ConcertUsecase concertUsecase;

    @Test
    void createToken() {
        //given
        Mockito.doReturn(
                User.builder().uuid(null).build()
        ).when(userReader).readById(Mockito.anyLong());
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build()
                )
        ).when(waitingQueueReader).readAllActiveTokens();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<WaitingQueue> tokenCaptor =ArgumentCaptor.forClass(WaitingQueue.class);

        // when
        concertUsecase.createToken(1L);

        Mockito.verify(userModifier).modifyUser(userCaptor.capture());
        Mockito.verify(waitingQueueWriter).writeToken(tokenCaptor.capture());
        User capturedUser = userCaptor.getValue();
        WaitingQueue capturedToken = tokenCaptor.getValue();

        // then
        Assertions.assertThat(capturedUser.getUuid()).isNotNull();
        Assertions.assertThat(capturedToken.getStatus()).isEqualTo(WaitingQueueStatus.WAIT);
    }

    @Test
    void getActiveToken() {
        // given
        Mockito.doReturn(WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build()).when(waitingQueueReader).readValidToken(Mockito.any());

        // when
        GetTokenResponse response = concertUsecase.getToken(UUID.randomUUID().toString());

        // then
        Assertions.assertThat(response.waitingNumber()).isEqualTo(0L);
    }

    @Test
    void getWaitToken() {
        // given
        Mockito.doReturn(
                WaitingQueue.builder()
                        .id(20L)
                        .status(WaitingQueueStatus.WAIT)
                        .build()
        ).when(waitingQueueReader).readValidToken(Mockito.any());

        Mockito.doReturn(
                WaitingQueue.builder()
                        .id(10L)
                        .status(WaitingQueueStatus.ACTIVE)
                        .build()
        ).when(waitingQueueReader).readActiveTokenWithMaxId();

        // when
        GetTokenResponse response = concertUsecase.getToken(UUID.randomUUID().toString());

        // then
        Assertions.assertThat(response.waitingNumber()).isEqualTo(10L);
    }

    @Test
    void updateWaitingQueue() {
        // given
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build()
                )
        ).when(waitingQueueReader).readAllActiveTokensWithLock();
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.WAIT)
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.WAIT)
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.WAIT)
                                .build()
                )
        ).when(waitingQueueReader).readWaitTokensLimitBy(Mockito.any());

        ArgumentCaptor<PageRequest> pageCaptor = ArgumentCaptor.forClass(PageRequest.class);

        // when
        concertUsecase.updateWaitingQueue();

        // then
        Mockito.verify(waitingQueueReader).readWaitTokensLimitBy(pageCaptor.capture());
        Mockito.verify(waitingQueueModifier, Mockito.times(3)).modifyToken(Mockito.any());

        PageRequest pageRequest = pageCaptor.getValue();
        Assertions.assertThat(pageRequest.getPageSize()).isEqualTo(7);
    }

    @Test
    void reserveSeat() {
        // given
        Mockito.doReturn(User.builder().build()).when(userReader).readByUuid(Mockito.anyString());
        Mockito.doReturn(WaitingQueue.builder().build()).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());
        Mockito.doReturn(Concert.builder().id(1L).build()).when(concertReader).getByDate(Mockito.any());
        Mockito.doReturn(Seat.builder().status(SeatStatus.AVAILABLE).build()).when(seatReader).readAvailableSeatByConcertIdAndNumberWithLock(Mockito.anyLong(), Mockito.anyLong());

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        ArgumentCaptor<Seat> seatCaptor = ArgumentCaptor.forClass(Seat.class);
        ArgumentCaptor<WaitingQueue> tokenCaptor = ArgumentCaptor.forClass(WaitingQueue.class);

        // when
        concertUsecase.reserveSeat(LocalDate.now(), 10L, UUID.randomUUID().toString());
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
    void pay() {
        // given
        Mockito.doReturn(User.builder().balance(10000L).build()).when(userReader).readByUuidWithLock(Mockito.anyString());
        Mockito.doReturn(WaitingQueue.builder().build()).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());
        Mockito.doReturn(Reservation.builder().seatCost(6000L).build()).when(reservationReader).readByIdWithLock(Mockito.anyLong());

        ArgumentCaptor<WaitingQueue> tokenCaptor = ArgumentCaptor.forClass(WaitingQueue.class);
        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        concertUsecase.pay(1L, UUID.randomUUID().toString());

        Mockito.verify(waitingQueueModifier).modifyToken(tokenCaptor.capture());
        Mockito.verify(userModifier).modifyUser(userCaptor.capture());
        Mockito.verify(reservationModifier).modifyReservation(reservationCaptor.capture());

        WaitingQueue capturedToken = tokenCaptor.getValue();
        User capturedUser = userCaptor.getValue();
        Reservation capturedReservation = reservationCaptor.getValue();

        // then
        Assertions.assertThat(capturedToken.getStatus()).isEqualTo(WaitingQueueStatus.EXPIRED);
        Assertions.assertThat(capturedUser.getBalance()).isEqualTo(4000L);
        Assertions.assertThat(capturedReservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    void updateSeat() {
        // given
        Mockito.doReturn(
                List.of(
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .build(),
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build(),
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .build()
                )
        ).when(reservationReader).readAllPaymentRequiredWithLock();
        Mockito.doReturn(Seat.builder().build()).when(seatReader).getById(Mockito.any());

        // when
        concertUsecase.updateSeat();

        // then
        Mockito.verify(seatReader, Mockito.times(2)).getById(Mockito.any());;
    }

    @Test
    void chargePoint() {
        // given
        Mockito.doReturn(User.builder().balance(0L).build()).when(userReader).readByUuidWithLock(Mockito.anyString());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        concertUsecase.chargePoint(10000L, UUID.randomUUID().toString());

        Mockito.verify(userModifier).modifyUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        //then
        Assertions.assertThat(capturedUser.getBalance()).isEqualTo(10000L);
    }
}