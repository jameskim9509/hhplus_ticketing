package kr.hhplus.be.server.api.payment.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationUnitTest {
    @Mock
    private UserReader userReader;
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private WaitingQueueModifier waitingQueueModifier;
    @Mock
    private UserModifier userModifier;
    @Mock
    private ReservationModifier reservationModifier;
    @Mock
    private ReservationReader reservationReader;
    @Mock
    private PaymentWriter paymentWriter;

    @InjectMocks
    private PaymentApplication paymentApplication;

    @Test
    void pay() {
        // given
        UserContext.setContext(
                User.builder()
                        .id(1L)
                        .uuid(UUID.randomUUID().toString())
                        .balance(10000L)
                        .build()
        );

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
        paymentApplication.pay(1L);

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
    void 활성화되지_않은_토큰으로_결제시_에러()
    {
        // given
        UserContext.setContext(
                User.builder()
                        .id(1L)
                        .uuid(UUID.randomUUID().toString())
                        .balance(10000L)
                        .build()
        );

        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidTokenByUuidWithLock(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> paymentApplication.pay(
                                5L
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }

    @Test
    void 만료된_예약_결제시_에러()
    {
        // given
        UserContext.setContext(
                User.builder()
                        .id(1L)
                        .uuid(UUID.randomUUID().toString())
                        .balance(10000L)
                        .build()
        );

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
                        () -> paymentApplication.pay(
                                5L
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("만료된 예약입니다.");
    }

    @Test
    void 결제가_필요없는_예약_결제시_에러()
    {
        // given
        UserContext.setContext(
                User.builder()
                        .id(1L)
                        .uuid(UUID.randomUUID().toString())
                        .balance(10000L)
                        .build()
        );

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
                        () -> paymentApplication.pay(
                                5L
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("결제가 필요한 예약이 아닙니다.");
    }

    @Test
    void 요청자가_예약하지_않은_예약_결제시_에러()
    {
        // given
        UserContext.setContext(
                User.builder()
                        .id(1L)
                        .uuid(UUID.randomUUID().toString())
                        .balance(10000L)
                        .build()
        );

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
                        () -> paymentApplication.pay(
                                5L
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("요청자의 예약 정보가 아닙니다.");
    }
}