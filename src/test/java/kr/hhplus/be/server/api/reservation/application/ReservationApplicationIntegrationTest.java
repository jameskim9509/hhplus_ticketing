package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@ActiveProfiles("test")
@SpringBootTest
class ReservationApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Autowired
    private ReservationApplication reservationApplication;

    @Transactional
    @Test
    void reserveSeat() {
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

        // when
        Reservation reservation =
                reservationApplication.reserveSeat(
                        LocalDate.of(2025, 7, 1),
                        25L,
                        user.getUuid()
                );

        // then
        Assertions.assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAYMENT_REQUIRED);
        Assertions.assertThat(reservation.getSeat().getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    void 동시에_3번_예약하면_2번_오류()
    {
        // given
        User user = userJpaRepository.save(
                User.builder()
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

        // when
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            reservationApplication.reserveSeat(
                                    LocalDate.of(2025,7,1),
                                    30L,
                                    user.getUuid()
                            );
                            return true;
                        } catch (RuntimeException re) {
                            System.out.println(re.getMessage());
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