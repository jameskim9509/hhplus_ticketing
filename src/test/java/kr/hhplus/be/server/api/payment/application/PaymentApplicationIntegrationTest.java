package kr.hhplus.be.server.api.payment.application;

import kr.hhplus.be.server.api.reservation.application.ReservationApplication;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@ActiveProfiles("test")
@SpringBootTest
class PaymentApplicationIntegrationTest {
    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Autowired
    PaymentApplication paymentApplication;

    @Transactional
    @Test
    void pay() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(100_000L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        WaitingQueue token = WaitingQueue.builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build();
        token.setUser(user);
        waitingQueueJpaRepository.save(token);
        waitingQueueJpaRepository.flush();

        Reservation reservation = reservationJpaRepository.save(
                Reservation.builder()
                        .user(user)
                        .seatCost(30_000L)
                        .status(ReservationStatus.PAYMENT_REQUIRED)
                        .expiredAt(LocalDateTime.now().plusMinutes(1))
                        .build()
        );
        reservationJpaRepository.flush();

        // when
        Payment payment =
                paymentApplication.pay(
                        reservation.getId(),
                        user.getUuid()
                );

        // then
        Assertions.assertThat(payment.getReservation().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        Assertions.assertThat(payment.getUser().getBalance()).isEqualTo(70_000L);
    }

    @Test
    void 동시에_3번_결제하면_2번_오류()
    {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(100_000L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        WaitingQueue token = WaitingQueue.builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build();
        token.setUser(user);
        waitingQueueJpaRepository.save(token);
        waitingQueueJpaRepository.flush();

        Reservation reservation =
                reservationJpaRepository.save(
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .user(user)
                                .seatCost(10000L)
                                .expiredAt(LocalDateTime.now().plusMinutes(5))
                                .build()
                );
        reservationJpaRepository.flush();

        // when
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            paymentApplication.pay(
                                    reservation.getId(),
                                    user.getUuid()
                            );
                            return true;
                        } catch (RuntimeException re) {
                            return false;
                        }
                    }
            ));
        }

        CompletableFuture[] futuresArray = futures.toArray(new CompletableFuture[futures.size()]);

        CompletableFuture.allOf(futuresArray)
                .thenRun(() ->
                {
                    Long success_count = futures.stream()
                            .filter(f -> {
                                try {
                                    return !f.get();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .count();

                    Assertions.assertThat(success_count).isEqualTo(2L);
                }).join();
    }
}