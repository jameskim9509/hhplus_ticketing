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
        Mockito.doReturn(
                User.builder()
                        .id(1L)
                        .balance(10000L)
                        .build()
        ).when(userReader).readByUuidWithLock(Mockito.anyString());

        Mockito.doReturn(
                WaitingQueue.builder()
                        .status(WaitingQueueStatus.ACTIVE)
                        .build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());

        Mockito.doReturn(
                Reservation.builder()
                        .seatCost(6000L)
                        .expiredAt(LocalDateTime.now().plusMinutes(1))
                        .status(ReservationStatus.PAYMENT_REQUIRED)
                        .user(User.builder().id(1L).build())
                        .build()
        ).when(reservationReader).readByIdWithLock(Mockito.anyLong());

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
        concertUsecase.updateSeat();

        // then
        Mockito.verify(reservationModifier, Mockito.times(2))
                .modifyReservation(Mockito.any());;
    }

    @Test
    void chargePoint() {
        // given
        Mockito.doReturn(
                User.builder()
                        .balance(0L)
                        .build()
        ).when(userReader).readByUuidWithLock(Mockito.anyString());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        concertUsecase.chargePoint(10000L, UUID.randomUUID().toString());

        Mockito.verify(userModifier).modifyUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        //then
        Assertions.assertThat(capturedUser.getBalance()).isEqualTo(10000L);
    }

    @Test
    void 이미_토큰이_존재하는데_재발급_받을_경우_에러()
    {
        // given
        Mockito.doReturn(User.builder().build())
                        .when(userReader).readById(Mockito.anyLong());
        Mockito.doReturn(true)
                .when(waitingQueueReader)
                .isValidTokenExists(Mockito.any());

        // when
        Assertions.assertThatThrownBy(
                () -> concertUsecase.createToken(1L)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("토큰이 이미 존재합니다.");
    }

    @Test
    void 활성화되지_않은_토큰으로_콘서트_조회시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidToken(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                () -> concertUsecase.getAvailableConcerts(
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        UUID.randomUUID().toString()
                )
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("활성화되지 않은 토큰입니다.");
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
                        () -> concertUsecase.getAvailableSeatsByDate(
                                LocalDate.now(),
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
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
                        () -> concertUsecase.reserveSeat(
                                LocalDate.now(),
                                30L,
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }

    @Test
    void 활성화되지_않은_토큰으로_결제시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> concertUsecase.pay(
                                5L,
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }

    @Test
    void 만료된_예약_결제시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());
        Mockito.doReturn(
                Reservation.builder()
                        .expiredAt(LocalDateTime.now().minusMinutes(1))
                        .build()
        ).when(reservationReader).readByIdWithLock(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                    () -> concertUsecase.pay(
                            5L,
                            UUID.randomUUID().toString()
                    )
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("만료된 예약입니다.");
    }

    @Test
    void 결제가_필요없는_예약_결제시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder()
                        .status(WaitingQueueStatus.ACTIVE)
                        .build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());
        Mockito.doReturn(
                Reservation.builder()
                        .expiredAt(LocalDateTime.now().plusMinutes(5))
                        .status(ReservationStatus.RESERVED)
                        .build()
        ).when(reservationReader).readByIdWithLock(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> concertUsecase.pay(
                                5L,
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("결제가 필요한 예약이 아닙니다.");
    }

    @Test
    void 요청자가_예약하지_않은_예약_결제시_에러()
    {
        // given
        Mockito.doReturn(User.builder().id(1L).build())
                        .when(userReader).readByUuidWithLock(Mockito.anyString());

        Mockito.doReturn(
                WaitingQueue.builder()
                        .status(WaitingQueueStatus.ACTIVE)
                        .build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.anyString());

        Mockito.doReturn(
                Reservation.builder()
                        .expiredAt(LocalDateTime.now().plusMinutes(5))
                        .status(ReservationStatus.PAYMENT_REQUIRED)
                        .user(User.builder().id(2L).build())
                        .build()
        ).when(reservationReader).readByIdWithLock(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> concertUsecase.pay(
                                5L,
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("요청자의 예약 정보가 아닙니다.");
    }
}